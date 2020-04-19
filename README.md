File Server Core

TODO:
- make start and end date optional
- let client have multiple certificates registered
- add registerFile to soap interface and add direct HTTP file upload (via PUT) to obtained url (=virtualPath)? 
- let different clients download the same file?
- add option to encrypt files with client's certificate (and implement .certificate extension)
- add directory structure to store files
- add WebDav support
- add S3 support
- add REST interface

- implement file upload (for (HTTP,) WebDav and S3)