# Magnolia Module Shop

A simple shop implementation for Magnolia

## Installing
Maven is the easiest way to install the module. Add the following dependency to your [bundle](https://documentation.magnolia-cms.com/display/DOCS54/Bundles+and+webapps).
```xml
<dependency>
  <groupId>info.magnolia.shop</groupId>
  <artifactId>magnolia-module-shop</artifactId>
  <version>4.0.0</version>
</dependency>
```

## Documentation
The documentation can be found [here](https://documentation.magnolia-cms.com/display/DOCS53/Shop+module).

## Versions and compatibility table

### Compatiblity

| Module Version | Magnolia Bundle Version
| ---------------|:-------------------------
| 4.0.x          | 6.2.10 
| 3.0.x          | 5.5 >

#### 4.0.x
For the shop version 4.0.x it's necessary to use the `ProxyClassDescriptor` instead of the `ClassDescriptor`.
For more information see the documentation of the [OCM module](https://github.com/magnolia-community/ocm).
