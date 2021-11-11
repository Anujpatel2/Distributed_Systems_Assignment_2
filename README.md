# Distributed_Systems_Assignment_2
Code which takes in random data which contains words and ip addresses. Code cleans data for just ip addresses and places them one per row. Next it returns the hostname for each ip addresses and saves it on server side. Client can then choose to download the file when needed.

To run the application, write the following codes on seperate command line terminals

rmiregistry
java -Djava.security.policy=policy.txt FileServer
java FileClient fileName machineName upload/download
