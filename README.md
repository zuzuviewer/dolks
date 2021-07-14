# dolks

transfer pdf to docx

## dependencies

It is depended on spire.doc.free.3.9.0 and spire.pdf.free-4.4.1(https://www.e-iceblue.com/)

## Functions

Transfer pdf document to docx document

## Issues

Docx document have watermarked,because it is free.If want docx documents without watermark,you can buy Commercial Edition in https://www.e-iceblue.com/ 

## Usage

If project is Maven,add below content to pom.xml

```xml
    <repositories>
        <repository>
            <id>com.e-iceblue</id>
            <name>e-iceblue</name>
            <url>https://repo.e-iceblue.com/nexus/content/groups/public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>e-iceblue</groupId>
            <artifactId>spire.pdf.free</artifactId>
            <version>4.4.1</version>
        </dependency>
        <dependency>
            <groupId> e-iceblue </groupId>
            <artifactId>spire.doc.free</artifactId>
            <version>3.9.0</version>
        </dependency>
    </dependencies>
```

## Explanation

It just for study and communication.If you use it with illegal to usage,  I had nothing to do with the matter