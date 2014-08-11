# Export Library

## Introduction
This package contains some of the code that is deployed at the DCC. Within this project there are three major products that make up a portion of the DCC infrastructure. These three projects are described below:

### Data Schema 
The project exportlibrary.datastructure contains all of the XSD's that are in turn compiled into exportlibrary.xsdvalidation that aids the reading and writing of data in the format that is accepted at the DCC. Using exportlibrary.xmlserialization these data can be stored in a database.

### XML Validation
This project exportlibrary.xmlvalidation compares data described in the XSD with various data sources that make sure that these data meets the minimum standard of correctness.

### Data Export
The project exportlibrary.exporterworker accepts a list of instruction that are generated externally to this projects and writes them to files that exported to the central data archive. 
