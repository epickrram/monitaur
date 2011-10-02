$(document).ready(function()
{
    retrieveData();
});

function retrieveData()
{
    $.post("/monitaur/data", {}, handleData, "json");
}

function handleData(jsonData)
{
    var monitorData = eval(jsonData);
    var logicalNameList = "";
    var dataSeries = [];
    var logicalNameCount = 0;
    var latestUpdate = -1;

    for(var logicalName in monitorData)
    {
        var data = [];
        for(var i = 0, n = monitorData[logicalName].length; i < n; i++)
        {
            var dataItem = monitorData[logicalName][i];
            var dataArrayForHost = data[dataItem.host];
            if(!dataArrayForHost)
            {
                data[dataItem.host] = [];
                dataArrayForHost = data[dataItem.host];
            }

            dataArrayForHost.push([dataItem.timestamp, dataItem.datum]);
            if(dataItem.timestamp > latestUpdate)
            {
                latestUpdate = dataItem.timestamp;
            }
        }
        dataSeries[logicalName] = data;

        logicalNameCount++;
        logicalNameList += logicalName + " (" + dataSeries[logicalName].length + ")<br/>";
        var graphElement = "placeholder-" + sanitiseLogicalName(logicalName);
        try
        {
            if(!$("#" + graphElement) || $("#" + graphElement).length === 0)
            {
                $("body").append("<div><span class='graphTitle'>" + logicalName + "</span><div id='" + graphElement + "'></div></div>");
                $("#" + graphElement).addClass("graphElement");
            }
            var placeholder = $("#" + graphElement);

            var plotData = [];
            for(host in dataSeries[logicalName])
            {
                plotData.push({label: host, data:dataSeries[logicalName][host]});
            }


            $.plot(placeholder, plotData, {
               xaxes:  [{ mode: 'time'}]

            });

        }
        catch(e)
        {
            $("error").append(e);
        }
    }

    if(latestUpdate !== -1)
    {
        $("#lastUpdate").html("<span>Last Update: " + new Date(latestUpdate).toTimeString() + "</span>");
        if(new Date().getTime() > latestUpdate + (10 * 1000))
        {
            $("#lastUpdate").addClass("error");
        }
    }
    else
    {
        $("#lastUpdate").html("Awaiting data");
    }
    
    setTimeout("retrieveData()", 5000);
}

function sanitiseLogicalName(logicalName)
{
    return logicalName.replace(" ", "_");
}