## File Server Core

### TODO
#### Download
*   add option to encrypt files with client's certificate (and implement .certificate extension)  
    *   let client have separate encryption certificate registered  
*   register multiple client certificates  
*   add clientId to createClient response  
*   add registerFile operation to soap interface to register a file from a different upload directory outside but available to fs-service?
*   add directory structure to store files  
*   add REST interface

#### Upload
*   implement file upload using tus protocol
