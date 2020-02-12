File Server Core

TODO:
- store size and checksum (and checksum type) in fsFile?
- add registerFile to soap interface and add direct HTTP file upload (via PUT) to obtained url (=virtualPath)? 
- let client have multiple certificates registered
- let different clients download the same file?
- add option to encrypt files with client's certificate
- add directory structure to store files
- add WebDav support
- add S3 support
- add REST interface

- implement file upload (for (HTTP,) WebDav and S3)