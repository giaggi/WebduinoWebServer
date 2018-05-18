/**
 * Created by giaco on 27/10/2017.
 */
var $systemPanel;
var $systemActuatorRow;
var $systemZoneRow;
var $systemScenarioRow;
var $systemServiceRow;
var $system;

function loadActuators(actuators) {
    var tbody = $systemPanel.find('tbody[name="actuatorlist"]');
    tbody[0].innerHTML = "";
    if (actuators != null) {
        $.each(actuators, function (idx, elem) {
            var actuator = $systemActuatorRow.clone();
            actuator.find('td[name="id"]').text(elem.id);
            actuator.find('td[name="actuatorid"]').text(elem.actuatorid);
            actuator.find('td[name="name"]').text(elem.name);
            actuator.attr("actuatorid", elem.actuatorid);
            actuator.click(function () {
                var actuatorid = $(this).attr("actuatorid");
                getWebduinoSystemActuator(actuatorid, function (actuator) {
                    loadWebduinoSystemActuator(actuator);
                });
            });
            tbody.append(actuator);
        });
    }
    var addbutton = $systemPanel.find('button[name="addactuator"]').click(function () {
        getSensors(function (sensors) {
            var actuator = {
                "webduinosystemid": $system.id,
                "id": 0,
                "actuatorid": sensors[0].id,
                "enabled": true,
            };
            loadWebduinoSystemActuator(actuator);
        });
    });
}

function loadZones(zones) {
    var tbody = $systemPanel.find('tbody[name="zonelist"]');
    tbody[0].innerHTML = "";
    if (zones != null) {
        $.each(zones, function (idx, elem) {
            var zone = $systemZoneRow.clone();
            zone.find('td[name="id"]').text(elem.id);
            zone.find('td[name="zoneid"]').text(elem.zoneid);
            zone.find('td[name="name"]').text(elem.name);
            zone.attr("zoneid", elem.zoneid);
            zone.click(function () {
                var zoneid = $(this).attr("zoneid");
                getWebduinoSystemZone(zoneid, function (zone) {
                    loadWebduinoSystemZone(zone);
                });
            });
            tbody.append(zone);
        });
    }
    var addbutton = $systemPanel.find('button[name="addzone"]').click(function () {
        getZones(function (zones) {
            var zone = {
                "webduinosystemid": $system.id,
                "id": 0,
                "zoneid": zones[0].id,
                "enabled": true,
            };
            loadWebduinoSystemZone(json);
        });
    });
}

function loadServices(webduinosystemservices) {
    var tbody = $systemPanel.find('tbody[name="servicelist"]');
    tbody[0].innerHTML = "";
    if (webduinosystemservices != null) {
        $.each(webduinosystemservices, function (idx, elem) {
            var service = $systemServiceRow.clone();
            service.find('td[name="id"]').text(elem.id);
            ctuator.find('td[name="serviceid"]').text(elem.serviceid);
            service.find('td[name="name"]').text(elem.name);
            service.attr("serviceid", elem.serviceid);
            service.click(function () {
                var serviceid = $(this).attr("serviceid");
                getWebduinoSystemService(serviceid, function (service) {
                    loadWebduinoSystemService(service);
                });
            });
            tbody.append(service);
        });
    }
    var addbutton = $systemPanel.find('button[name="addservice"]').click(function () {
        getServices(function (services) {
            var service = {
                "webduinosystemid": $system.id,
                "id": 0,
                "serviceid": services[0].id,
                "enabled": true,
            };
            loadWebduinoSystemService(json);
        });
    });
}

function loadScenarios(scenarios) {
    var tbody = $systemPanel.find('tbody[name="scenariolist"]');
    tbody[0].innerHTML = "";
    if (scenarios != null) {
        $.each(scenarios, function (idx, scenario) {
            var scenariorow = $systemScenarioRow.clone();
            scenariorow.find('td[name="id"]').text(scenario.id);
            scenariorow.find('td[name="name"]').text(scenario.name);
            scenariorow.find('td[name="enabled"]').text(scenario.enabled);
            scenariorow.find('td[name="status"]').text(scenario.status);
            scenariorow.attr("scenarioid", scenario.id);
            scenariorow.click(function () {
                var scenarioid = $(this).attr("scenarioid");
                getWebduinoSystemScenario(scenarioid, function (scenario) {
                    loadWebduinoSystemScenario(scenario);
                })
            });
            tbody.append(scenariorow);

        });
    }
    var addbutton = $systemPanel.find('button[name="addscenario"]').click(function () {
        var scenario = {
            "webduinosystemid": $system.id,
            "id": 0,
            "name": "nuovo scenario",
            "description": "descrizione scenario",
            "enabled": true,
        };
        var emptyArray = [];
        scenario["programs"] = emptyArray;
        scenario["timeintervals"] = emptyArray;
        scenario["triggers"] = emptyArray;
        postData("webduinosystemscenario", scenario, function (result, response) {
            if (result) {
                notification.show();
                notificationsuccess.find('label[name="description"]').text("timerange salvato");
                var json = jQuery.parseJSON(response);
                loadWebduinoSystemScenario(json);
            } else {
                notification.show();
                notification.find('label[name="description"]').text(response);
            }
        });
    });
}

function loadWebduinoSystem(system) {

    $system = system;

    $("#result").load("webduinosystem.html", function () {
            // back button
            backbutton.unbind("click");
            backbutton.click(function () {
                loadWebduinoSystems();
            });
            pagetitle.text('Sistema');
            notification.hide();
            notificationsuccess.hide();

            $systemPanel = $(this).find('div[id="panel"]');
            $systemActuatorRow = $systemPanel.find('tr[name="actuatorrow"]');
            $systemZoneRow = $systemPanel.find('tr[name="zonerow"]');
            $systemServiceRow = $systemPanel.find('tr[name="servicerow"]');
            $systemScenarioRow = $systemPanel.find('tr[name="scenariorow"]');

            $systemPanel.find('p[name="headingright"]').val(system.id);
            $systemPanel.find('div[name="name"]').text(system.name);

            // actuators
            loadActuators($system.actuators);
            // zoines
            loadZones($system.zones);
            // services
            loadServices($system.services);
            // scenarios
            loadScenarios($system.scenarios);
        }
    );
}
