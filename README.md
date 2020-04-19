## File Server Core

### TODO
*   make start and end date optional  
*   use real filename in download???  
*   add option to encrypt files with client's certificate (and implement .certificate extension)  
    *   let client have separate encryption certificate registered  
*   let client have multiple certificates registered  
*   remove client certificate option form fs-service  
*   add clientId to createClient response  
*   add registerFile to soap interface to register a file from a different upload directory outside but available to fs-service?
*   add directory structure to store files  
*   add WebDav support  
*   add S3 support  
*   add REST interface  

*   implement file upload (for (HTTP,) WebDav and S3)