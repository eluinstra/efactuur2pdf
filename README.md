### efactuur2pdf

- PDF generation for UBL or SETU invoice documents (efacturen)
- XSD, Schematron and Genericode validation for EBF messages

The following HR-XML-NL and UBL-NL message versions are currently supported in this project :
- NLCIUS
- si-ubl-2.0.3.5
- UBL Invoice 1.9
- UBL Invoice 1.8
- UBL Invoice 1.7
- UBL Invoice 1.6.3
- UBL Invoice 1.6.2
- UBL Invoice 1.1
- SETU Invoice 2.0
- SETU Invoice 1.8.1
- SETU Invoice 1.7
- SETU Invoice 1.6.4
- SETU Invoice 1.1

### Usage

Validate file

```
java -jar efactuur2pdf-3.0.0.jar nl.ordina.digikoppeling.ebf.ValidateFile <filename>
```

Validate all files in directory

```
java -jar efactuur2pdf-3.0.0.jar nl.ordina.digikoppeling.ebf.ValidateDir <path>
```

Transform file

```
java -jar efactuur2pdf-3.0.0.jar nl.ordina.digikoppeling.ebf.TransformFileToPDF <filename>
```

Transform all files in directory

```
java -jar efactuur2pdf-3.0.0.jar nl.ordina.digikoppeling.ebf.TransformDirToPDFs <path>
```

### Building

#### Packaging

```bash
mvn clean package
```

#### Reporting

```bash
mvn site
# or to generate individual reports:
mvn jacoco:report
mvn cobertura:cobertura
mvn org.owasp:dependency-check-maven:check
mvn checkstyle:checkstyle
mvn jdepend:generate
mvn jxr:jxr
mvn jxr:test-jxr
mvn pmd:pmd
mvn surefire:test
mvn com.github.spotbugs:spotbugs-maven-plugin:check
mvn com.github.spotbugs:spotbugs-maven-plugin:spotbugs
mvn taglist:taglist
```
