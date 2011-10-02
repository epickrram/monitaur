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

    for(var monitoredDataType in monitorData)
    {
        var data = [];
        for(var i = 0, n = monitorData[monitoredDataType].length; i < n; i++)
        {
            var dataItem = monitorData[monitoredDataType][i];
            data.push([dataItem.timestamp, dataItem.datum]);
        }
        dataSeries[monitoredDataType] = data;

        logicalNameCount++;
        logicalNameList += monitoredDataType + " (" + dataSeries[monitoredDataType].length + ")<br/>";
    }
    var counter = 0;
    for(var i in dataSeries)
    {
        $.plot($("#placeholder" + counter++), [dataSeries[i]], {
               xaxes:  [{ mode: 'time'}]
            
           });
    }

    $("#data").replaceWith("<div id='data'>" + logicalNameList + "</div>");
    setTimeout("retrieveData()", 5000);
}