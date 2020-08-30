# File Server Core
Implementents the core for an HTTP file up and download server. It provides file download over HTTPS using HTTP GET (Ranges are supported) and file upload using the tus protocol. It can be used for Grote Berichten file transfer.  

### TODO
##### Download
*   add option to encrypt files with user's certificate (and implement .certificate extension)  
    *   register separate encryption certificate(s)  
*   register multiple user certificates (with start-date?)
*   add registerFile operation to soap interface to register a file from a different upload directory outside but available to file-server?
*   add multilevel directory structure to store more files more efficiently  
*   add REST interface

##### Upload
*   implement tus concatenation


### Development
##### Eclipse

install: https://marketplace.eclipse.org/content/m2e-apt
