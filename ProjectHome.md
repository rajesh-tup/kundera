
---


---

# Please Note #
## Kundera code base has been shifted to GitHub (at https://github.com/impetus-opensource/Kundera). ##


---


---



## kundera production deployment examples can be found  [here](https://github.com/impetus-opensource/Kundera/wiki/Kundera-Production-deployments) ##


# Version Alpha #

## Annotation based java library for Cassandra database ##

The idea behind Kundera is to make working with Cassandra drop-dead simple and fun. Kundera does not reinvent the wheel by making another client library; rather it leverages the existing libraries and builds - on top of them - a wrap-around API to help developers do away with unnecessary boiler plate codes, and program a neater-and-cleaner code that reduces code-complexity and improves quality. And above all, improves productivity.

### Up and running in 5 minutes ###

Understanding Kundera only takes 5 minutes. Please read the following introductory article http://anismiles.wordpress.com/2010/06/30/kundera-knight-in-the-shining-armor/

## Objectives ##

  * To completely remove unnecessary details, such as Column lists, Super Column lists, byte arrays, Data encoding etc.
  * To be able to work directly with Domain models just with the help of annotations
  * To eliminate “code plumbing”, so as to keep the flow of data processing clear and obvious
  * To completely separate out Cassandra and its obvious concerns from application-level logic for robust application development
  * To include the latest Cassandra developments without breaking anything

**Status**

Kundera aims to help complex commercial Cassandra projects. Development is ongoing but a key objective is to avoid breaking changes.



## About Us ##

kundera is backed by Impetus Labs - iLabs. iLabs is a R&D consulting division of Impetus Technologies (http://www.impetus.com). iLabs focuses on innovations with next generation technologies and creates practice areas and new products around them. iLabs is actively involved working on High Performance computing technologies, ranging from distributed/parallel computing, Erlang, grid softwares, GPU based software, Hadoop, Hbase, Cassandra, CouchDB and related technologies. iLabs is also working on various other Open Source initiatives.

## History behind kundera ##

After using Cassandra for java based projects, it was felt that there is a need of a JPA compliant ORM over Cassandra. Animesh intially wrote kundera core code for couples of days and kundera was born. We have nurtured it at iLabs and thought it is the right time to open source it to the Cassandra community for it to grow bigger and better.