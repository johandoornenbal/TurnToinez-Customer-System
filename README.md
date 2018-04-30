# TurnToinez-Customer-System

## An integrated backoffice application for a small business selling custom made leotards (gymnastic suits) through the internet.

The website of the business: http://www.turntoinez.nl

This application is build using the [Apache Isis](http://isis.apache.org/)

The application is using Dutch as main language. *) See some remarks on i18n under [support](#support).

### Functionality overview

#### Main supported proces
Order forms from the website are retreived using the website's REST api. Order forms then are processed by the user resulting in a Customer (Klant) and an Order (Bestelling).

The lifecycle of an order is
* draft (Klad)
* order (Bestelling) - 'Not yet paid' ('Nog niet betaald')
* paid (Betaald)
* finished (Klaar)

On entering order-status 'Bestelling' an Invoice is being made with a temporary invoice number. The invoice can be downloaded (as pfd) or send by email (as pdf attachment).
 
On entering paid-status 'Betaald' an entry in the financial register is made automatically.

#### Other processes supported
Outgoing financial entries (costs) can be booked. VAT can be calculated if desired. Quarterly and Annual reports can be produced.

The making of notes - on customer and order level.

Sending e-mail to customer.

Time registration - very basic.

Extensive auditing (of all user actions).

#### Login (user: test pw: pass)
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/login.png "Login Screen")

#### Homescreen
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/home.png "Home Screen")

#### Customer screen shots
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/customer.png "Customer Screen")
###### Collections on customer (collapsed)
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/customer2.png "Customer2 Screen")
###### Google Api is used to locate the customer
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/customer3.png "Customer3 Screen")

#### Order screen
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/order.png "Order Screen")

#### Invoice screen
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/invoice.png "Invoice Screen")

#### Produced invoice pdf
This pdf is generated using the factuur_template.docx file (making use of [freemarker](http://freemarker.org/))

![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/invoicepdf.png "Invoice pdf")

#### Financial booking entry
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/booking.png "Financial Booking entry")

#### Financial report
![alt text](https://raw.githubusercontent.com/johandoornenbal/TurnToinez-Customer-System/master/docs/images/report.png "Financial Report")

### Used Apache Isis Modules
The use of [apache isis add-ons](http://www.isisaddons.org/) provided a lot of functionality ready to be used in this application.

Among others the following are used:
* isis-module-excel
* isis-module-audit
* isis-module-flywaydb
* isis-wicket-excel
* isis-wicket-gmap3
* isis-wicket-wicketcharts
* incode-module-document

## Running the application

You can run the application for instance using `mvn jetty plugin` and using an in-memory HSQLDB database:
  
`cd webapp`   
`export ISIS_OPTS="isis.appManifest=domainapp.app.DomainAppAppManifestWithFixtures"` (in order to load some demo data)     
`mvn jetty:run`

Once the app has started, browse to: `http://localhost:8080/wicket/` and log in using user `test` pw: `pass` as credentials.

Another way is by using [jetty-runner](http://www.eclipse.org/jetty) when you don't use maven:

`export ISIS_OPTS="isis.appManifest=domainapp.app.DomainAppAppManifestWithFixtures"`  
`java -jar ./jetty-runner.jar --port 9090 ttiApp.war `
 
Or by using a host like Tomcat.
 
Other database connections can be configured in `persistor.properties`. 

SMTP server and other settings are managed in `isis.properties`.

## Support

You are free to adapt or extend this application to your needs.
If you would like assistance in doing so, go to http://www.yodo.nl or http://www.incode.org.

The application is using Dutch as main language. Some development for i18n has still to be done like additions to the `tranlations-en.po` file to start with.
When you consider to use (parts of) this application in another language please note that translations to the `xxx.layout.xml` files have to be made as well.

You can find plenty of help on using Apache Isis at the [Isis mailing lists](http://isis.apache.org/support.html).
There is also extensive [online documentation](http://isis.apache.org/documentation.html).

## Legal Stuff

#### License

```
Copyright 2017 Johan Doornenbal
```
```
Licensed under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
```
