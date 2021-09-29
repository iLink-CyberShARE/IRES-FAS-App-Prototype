/**
 * <h1> Connected Scatter Plot </h1>
 *
 * Metrics and algorithms for the measure of flooding level by the raspberry PI
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

var lim = 10;
var data = [];
var dateFormat = d3.timeFormat("%Y-%m-%d %H:%M:%S");
//Setting the dimensions and margins of the graph
var margin = {top: 50, right: 30, bottom: 40, left: 80},
    width = 460 - margin.left - margin.right,
    height = 400 - margin.top - margin.bottom;

var div = d3.select("body").append("div")
    .attr("class", "tooltip")
    .style("opacity", 0);

//Appending the svg object to the body of the page
var svg = d3.select("#plot")
    .append("svg")
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g")
    .attr("transform",
        "translate(" + margin.left + "," + margin.top + ")");

//Read the data
$.getJSON(ip_address+'raspi/retrieve?limit='+lim, function(result){
    $.each(result,function(i, d){
        var dict = {};
        if (d["severity"]=="Low"){
            dict['value'] = 0;
        }else if(d["severity"]=="Medium"){
            dict['value'] = 1;
        }else if(d["severity"]=="High"){
            dict['value'] = 2;
        }
        dict['date'] = d['date'];
        data.push(dict);
    });
    data.forEach(function(d){
        console.log(d);
        var parseDate = d3.timeParse("%Y-%m-%d %H:%M:%S");
        d.date = parseDate(d.date);
        console.log(d.date);
        d.value = +d.value;
    });
    console.log(data);
    //Title
    svg.append("text")
        .attr("x", (width / 2))
        .attr("y", 0 - (margin.top / 2))
        .attr("text-anchor", "middle")
        .style("font-size", "16px")
        .style("text-decoration", "underline")
        .text("Flooding Severity Over Time");
    console.log('A');
    // Add X axis --> it is a date format
    var x = d3.scaleTime()
        .domain(d3.extent(data, function(d) {return d.date; }))
        .range([ 0, width ]);
    svg.append("g")
        .attr("transform", "translate(0," + height + ")")
        .call(d3.axisBottom(x));

    //Add the text label for the x axis
    svg.append("text")
        .attr("transform", "translate(" + (width / 2) + " ," + (height + margin.bottom) + ")")
        .style("text-anchor", "middle")
        .text("Date (%H:%M)");

    console.log('B');
    //Add Y axis
    var y = d3.scaleLinear()
        .domain( [-0.1, 2.1])
        .range([ height, 0 ]);
    svg.append("g")
        .call(d3.axisLeft(y).tickFormat(function (d) {
            label = '';
            if(d == 0){
                label = 'Low';
            } else if(d == 1){
                label = 'Medium';
            } else if(d == 2){
                label = "High";
            }
            return label;
        }));

    //Add the text label for the Y axis
    svg.append("text")
        .attr("transform", "rotate(-90)")
        .attr("y", 10 - margin.left)
        .attr("x",0 - (height / 2))
        .attr("dy", "1em")
        .style("text-anchor", "middle")
        .text("Flooding Severity");

    console.log('C');
    //Add the line
    svg.append("path")
        .datum(data)
        .attr("fill", "none")
        .attr("stroke", "#69b3a2")
        .attr("stroke-width", 2)
        .attr("d", d3.line()
            .x(function(d) { return x(d.date) })
            .y(function(d) { return y(d.value) })
        );

    console.log('D');
    //Add the points
    svg
        .append("g")
        .selectAll("dot")
        .data(data)
        .enter()
        .append("circle")
        .attr("cx", function(d) { return x(d.date) } )
        .attr("cy", function(d) { return y(d.value) } )
        .attr("r", 5)
        .attr("stroke", "#32CD32")
        .attr("stroke-width", 1.5)
        .attr("fill", "#FFFFFF")
        .on('mouseover', function (d, i) {
            d3.select(this).transition()
                .duration('100')
                .attr("r", 7);

            //Makes div appear
            div.transition()
                .duration(100)
                .style("opacity", 1);
            label = dateFormat(d.date);
            div.html(label)
                .style("left", (d3.event.pageX - 40) + "px")
                .style("top", (d3.event.pageY - 30) + "px");
        })
        .on('mouseout', function (d, i) {
            d3.select(this).transition()
                .duration('200')
                .attr("r", 5);

            //makes div disappear
            div.transition()
                .duration('200')
                .style("opacity", 0);
        });
});
