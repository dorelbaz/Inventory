# Inventory Management System

## Project description

This system contains a server and a client. The client has a graphical interface of a food item database at his disposal and the server queries said database according to the client's requests. The communication is bi-directional and secured, utilizing RSA encryption.

``The Client`` has a graphical interface, made using *JavaFX*, where on successful connection with the server, one could monitor an 
existing database of food items, create a database of said items and operate on it. 
The operations are conducted on a single item, except for search and print, and are as follows: 
 * search
 * addition
 * update
 * delete
 * print

This system also supports __multiple clients operating simultaneously on a single, common database.__ The clients may operate on the __same network or different networks.__

**The client-side should contain:**
 * The .java files:
   + ClientController
   + ByteArrayWrapper
   + ClientThread
   + InventoryApplication
   + Product
   + RSA
 * The ClientKeys folder.
 * The ServerDetails folder.
 * Inventory.fxml


__NOTE:__ The client must provide the system with a ServerDetails.txt containing:
- The server's IP address:
    + Public IP address (IPv4) - if communication will be carried on different networks.
    + localhost or 127.0.0.1 - if communication will be carried on the same network.
- A port number for communication.

Place the file within the ServerDetails folder. __Run__ the Server.java before you run 
InventoryApplication.java.

``Food Item`` consists of: 
  * Name - A none-null string of size 32 at most,
  * Catalogue - An integer within the range (0,99999999],
      - Catalogue also serves as a primary key within the database.
  * Quantity - An integer within the range [0,999],
      - If an item has quantity 0 it will be automatically deleted from the database.
  * Production Date - A none-null date of the format DD.MM.YYYY,
  * Expiry Date - Same as production date,
  * Minimum Temperature - A float within the range [-99,999],
  * Maximum Temperature - Same as minimum temperature,
  * Price - A float within the range (0,9999].


``The Server`` at first establishes a connection with a __*PostgreSQL* server__, if successful the server begins to wait indefinitely for requests from the client. Requests recieved from the client, or clients, are forwarded to threads that construct the appropriate SQL statement and query the database. 
If the __query is successful__, the server will return the result with an accompanying, success message to the client 
and if the __query has failed__, the client will be notified accordingly. 

**The server-side should contain:**
 * The .java files:
   + Server
   + ByteArrayWrapper
   + ServerThread
   + Operations
   + SQLOperations
   + Add
   + Update
   + Delete
   + Search
   + Print
   + RSA
 * The ServerKeys folder.
 * The SQLServerDetails folder.

__NOTE:__ Provide the server with a SQLServerDetails.txt containing:
- The PostgreSQL server's URL.
  + For example:   __jdbc:postgresql://localhost:5432/Stock?user=postgres&password=1234__
- The same port number as mentioned in the client's NOTE. 

Place the file within the SQLServerDetails folder. __Launch__ the server before you launch 
the client.


## Setup
* Import the jar files:
  - postgresql-42.7.4.jar
  - WaifUPnP.jar
* Check version compatibility.

## Compatibility
* JavaFX 21.
* PostgreSQL driver 42.7.4.
* Scene Builder 21.0.0.
* Intellij 21.0.4.
