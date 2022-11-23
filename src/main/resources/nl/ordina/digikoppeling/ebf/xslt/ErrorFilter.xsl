<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:svrl="http://purl.oclc.org/dsdl/svrl" version="2.0">
  <xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
  <xsl:param name="failOnWarning" required="yes"/>

  <xsl:template match="svrl:schematron-output">
    <xsl:choose>
      <xsl:when test="$failOnWarning = 'true'">
        <xsl:apply-templates select="svrl:failed-assert"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="svrl:failed-assert[@flag = 'fatal']"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="svrl:failed-assert">
    <xsl:text>&#xa;</xsl:text>
    <xsl:value-of select="@flag"/><xsl:text>=</xsl:text><xsl:value-of select="svrl:text"/><xsl:text>&#xa;</xsl:text>
    <xsl:text>id=</xsl:text><xsl:value-of select="@id"/><xsl:text>&#xa;</xsl:text>
    <xsl:text>test=</xsl:text><xsl:value-of select="@test"/><xsl:text>&#xa;</xsl:text>
    <xsl:text>location=</xsl:text><xsl:value-of select="@location"/><xsl:text>&#xa;</xsl:text>
  </xsl:template>

</xsl:stylesheet>