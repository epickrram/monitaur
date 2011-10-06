var monitoringConfigList;

$(document).ready(function()
{
    retrieveData();
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
    alert("Updated config");
}

function retrieveData()
{
    $.post("/monitaur/config", {}, handleData, "json");
}

function handleData(jsonData)
{
    try
    {
        monitoringConfigList = eval(jsonData);
        var n = monitoringConfigList.length;
        for(var i = 0; i < n; i++)
        {
            var monitoringConfig = monitoringConfigList[i];
            var listItemName = monitoringConfig.objectName + "." + monitoringConfig.attributeName;
            var containerId = "li-" + i;
            $("#availableAttributeList").append("<div id='" + containerId + "'></div>");
            var container = $("#" + containerId);
            container.append("<span class='attributeName'>" + listItemName + "</span>");
            container.append("<span>Configured hosts:</span>")

            for(var j = 0, m = monitoringConfig.agentStates.length; j < m; j++)
            {
                var scopedMonitoringConfig = monitoringConfig;
                var agentState = scopedMonitoringConfig.agentStates[j];
                var agentId = agentState.agentId;
                var isMonitored = agentState.monitored;
                if(isMonitored)
                {
                    alert(monitoringConfig.attributeName + " is monitored");
                }

                container.append("<input id='ch-" + i + "-" + j + "' type='checkbox' " + ((isMonitored) ? "checked='true'" : "") + "><span id='chspan-" + i + "-" + j + "'>" + scopedMonitoringConfig.agentStates[j].agentId + "</span>");
                $("#ch-" + i + "-" + j).click(function(localAgentState)
                {
                    return function()
                    {
                        if($(this).attr("checked") == "true")
                        {
                            localAgentState.monitored = false;
                            $(this).attr("checked", "false")
                        }
                        else
                        {
                            localAgentState.monitored = true;
                            $(this).attr("checked", "true")
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
    catch(e)
    {
        $("error").append(e);
    }

    //setTimeout("retrieveData()", 5000);
}