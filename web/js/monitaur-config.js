var monitoringConfigList;
var fullAttributeNames = [];

$(document).ready(function()
{
    reset();

    $("#filter-text").keyup(function(event)
    {
        var matchedNothing = true;
        var text = $(this).attr("value");
        $("#filterValues").detach();
        $("#filterPanel").append("<div id='filterValues'></div>");

        for(var i = 0, n = fullAttributeNames.length; i < n; i++)
        {
            if(fullAttributeNames[i].indexOf(text) > -1)
            {
                $("#filterValues").append(fullAttributeNames[i] + "<br/>");
                matchedNothing = false;
            }
        }

        if(13 == event.keyCode)
        {
            $("#filter-text").attr("value", "");
            display(text);
            $("#filterPanel").addClass("hidden");
        }
        else if(text == '' || matchedNothing)
        {
            $("#filterPanel").addClass("hidden");
        }
        else
        {
            $("#filterPanel").removeClass("hidden");
        }
    }
    );
    $("#update-button").click(function()
    {
        var postData = [];
        for(var i = 0, n = monitoringConfigList.length; i < n; i++)
        {
            postData[i] = monitoringConfigList[i];
        }
        $.post("/monitaur/config?update", {"postData":  JSON.stringify(postData)}, onUpdateConfig, "json");
    });
});

function onUpdateConfig()
{
    $("#message").text("Updated config");
    reset();
}

function reset()
{
    retrieveData();
    $("#filter-text").attr("value", "");
}

function retrieveData()
{
    $.post("/monitaur/config", {}, handleData, "json");
}

function display(filterText)
{
    $("#availableAttributeList").detach();
    $("#availableAttributes").append("<div id='availableAttributeList'></div>");

    var n = monitoringConfigList.length;
    fullAttributeNames = [];
    for(var i = 0; i < n; i++)
    {
        var monitoringConfig = monitoringConfigList[i];
        var listItemName = monitoringConfig.attributeDetails.objectName + "." + monitoringConfig.attributeDetails.attributeName;
        if(monitoringConfig.attributeDetails.compositeKey != null)
        {
            listItemName += "." + monitoringConfig.attributeDetails.compositeKey;
        }
        fullAttributeNames.push(listItemName);
        if(listItemName.indexOf(filterText) > -1)
        {
            var containerId = "li-" + i;
            $("#availableAttributeList").append("<div id='" + containerId + "' class='attributeConfig'></div>");
            var container = $("#" + containerId);
            container.append("<div class='attributeName'>" + listItemName + "</div>");
            container.append("<span>Configured hosts:</span>")

            for(var j = 0, m = monitoringConfig.agentStates.length; j < m; j++)
            {
                var agentState = monitoringConfig.agentStates[j];
                var agentId = agentState.agentId;
                var isMonitored = agentState.monitored;
                
                container.append("<input id='ch-" + i + "-" + j + "' type='checkbox' " + ((isMonitored) ? "checked='true'" : "") + "><span id='chspan-" + i + "-" + j + "'>" + agentState.agentId + "</span>");
                $("#ch-" + i + "-" + j).click(function(localAgentState)
                {
                    return function()
                    {
                        if(localAgentState.monitored === false)
                        {
                            localAgentState.monitored = true;
                            $(this).attr("checked", "checked");
                        }
                        else
                        {
                            localAgentState.monitored = false;
                            $(this).removeAttr("checked");
                        }
                    };

                }(agentState));
            }
            container.append("<input type='checkbox'/>Monitor all agents");

            $("#li-" + i).click(function()
            {
                $("#d-" + i).removeClass("hidden");
            });
        }

    }
}

function handleData(jsonData)
{
    try
    {
        monitoringConfigList = eval(jsonData);

        display("");
    }
    catch(e)
    {
        $("error").append(e);
    }
}