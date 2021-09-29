# International Research Experiences for Students (IRES): U.S.-Mexico Interdisciplinary Research Collaboration for Smart Cities. Smart Flooding Alert System (Smart FAS) application prototype.

## Description
This repository forms part of the Smart FAS application prototype. The purpose of this application is to collect real-time flooding event data from user- and sensor-generated reports.

## Repository Content
- Flooding-mobile-app: Smart FAS Android Mobile App prototype.
- Flooding-server: Restful API for storing reports and displaying a map.
- Raspberry-Pi-Code: Computer Vision algorithm for detecting the water level in a flooding event using a raspberry PI.

## Instructions for creating the MongoDB database
1. Create a database:<br />
   - use app<br />
2. Create collections:<br />
   - db.createCollection('reports')<br />
   - db.createCollection('floodingUsers')<br />

## Instructions for running the server
1. Flooding Server: <br />
    - Enter to the following path:<br />
      Flooding-server/bin<br />
    - and run:<br />
      node www <br />

## Participating Institutions
+ University of Texas at El Paso (UTEP)
+ Universidad de Guadalajara (UdeG), Smart Cities Innovation Center
  
## IRES investigators
+ Dr. Ruey (Kelvin) Cheu - UTEP
+ Dr. Victor M. Larios Rosillo - UdeG
+ Dr. Oscar A. Mondragon Campos - UTEP
+ Dr. Natalia Villanueva Rosales - UTEP

## IRES contributing participants
+ Jonathan Avila
+ Ubaldo Castro
+ Jesus Garcia
+ Manuel Hernandez
+ Mario Marinelarena
+ Luis Ochoa
+ Cynthia Sustaita

## Acknowledgements
This material is based upon work supported by the National Science Foundation (NSF) Grant No. 1658733 IRES: U.S.-Mexico Interdisciplinary Research Collaboration for Smart Cities. This work used resources from Cyber-ShARE Center of Excellence, which is supported by NSF Grant No. HRD-1242122. Any opinions, findings, and conclusions or recommendations expressed in this material are those of the author(s) and do not necessarily reflect the views of the NSF.

## Copyright
&#169; 2018-2021, IRES: U.S.-Mexico Interdisciplinary Research Collaboration for Smart Cities investigators and contributing participants.

## Credits
+ Weather Data is provided by  OpenWeather (TM) licensed under CC BY-SA 4.0     
  https://creativecommons.org/licenses/by-sa/4.0/
+ Mobile app icons provided by Font Awesome by Dave Gandy - http://fontawesome.io licensed under SIL OFL 1.1   
+ Map Data by © OpenStreetMap Contributors under license Open Data Commons Open Database License (ODbL) - https://www.openstreetmap.org/
+ Leaflet libraries by Vladimir Agafonkin. Maps © OpenStreetMap contributors - https://leafletjs.com/

## Licenses
Data is Licensed under Attribution-ShareAlike 4.0 International (CC BY-SA 4.0) 

Sourde code is Licensed under the GNU General Public License v3.0, you may not use this file except in compliance with the License.
This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you are welcome to redistribute it under certain conditions.
