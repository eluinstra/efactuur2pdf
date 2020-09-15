<?xml version="1.0" encoding="utf-8"?>
<!--

    Copyright 2011 Clockwork

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:stylesheet xpath-default-namespace="http://www.clockwork.nl/ezp/pdf/canonical" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:fox="http://xmlgraphics.apache.org/fop/extensions" xmlns:nl="http://ns.hr-xml.org/2007-04-15" version="2.0">

	<xsl:decimal-format decimal-separator="," grouping-separator="." />
	<xsl:decimal-format name="european" decimal-separator="," grouping-separator="." />

	<xsl:variable name="date_format" select="'[D01]-[M01]-[Y]'"/>
	<xsl:variable name="date_time_format" select="'[H01]:[m01]:[s01] [D01]-[M01]-[Y]'"/>
	<xsl:variable name="decimal_format" select="'###.###.###.##0,00'"/>

	<xsl:param name="message_id" required="yes"/>
	<xsl:param name="message_format" required="yes"/>
	<xsl:param name="message_version" required="yes"/>
	<xsl:param name="bericht_soort" required="yes"/>
	<xsl:param name="message_date" required="yes" as="xs:dateTime" />
	<xsl:param name="original_message_type" required="yes"/>
																   
	<xsl:template match="/">
		<fo:root>
			<fo:layout-master-set>
				<fo:simple-page-master master-name="page" page-width="210mm" page-height="297mm" margin-top="5mm" margin-left="5mm" margin-right="5mm">
					<fo:region-body region-name="body" margin-top="1.5cm" margin-bottom="1cm"/>
					<fo:region-before region-name="header" extent="1.5cm"/>
					<fo:region-after region-name="footer" extent="1cm"/>
				</fo:simple-page-master>
				<fo:page-sequence-master master-name="content">
					<fo:repeatable-page-master-reference master-reference="page"/>
				</fo:page-sequence-master>
			</fo:layout-master-set>
			<xsl:call-template name="Content"/>
		</fo:root>
	</xsl:template>
	
	<xsl:template name="Content">
		<fo:page-sequence id="page-sequence" master-reference="content" font-family="Times" font-size="11pt" line-height="1.1">
			<fo:static-content flow-name="header">
				<xsl:call-template name="header"/>
			</fo:static-content>
			<fo:static-content flow-name="footer">
				<xsl:call-template name="footer"/>
			</fo:static-content>
			<fo:flow flow-name="body">
				<xsl:call-template name="body"/>
			</fo:flow>
		</fo:page-sequence>	
	</xsl:template>
	
	<xsl:template name="header">
		<fo:block>
			<fo:table>
				<fo:table-column column-width="30%"/>
				<fo:table-column column-width="70%"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left">
								BerichtSoort: <xsl:value-of select="$bericht_soort"/>
							</fo:block>			
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right">
								Berichtnummer: <xsl:value-of select="$message_id"/>
							</fo:block>
						</fo:table-cell>
												
					</fo:table-row>
					<fo:table-row>
						<fo:table-cell>
							<fo:block text-align="left">
								Invoice Formaat : <xsl:value-of select="$message_format"/> <xsl:value-of select="$message_version"/>
							</fo:block>							
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right">
								Ontvangst Digipoort: <xsl:value-of select="format-dateTime($message_date,$date_time_format)"/>
							</fo:block>
						</fo:table-cell>																	
					</fo:table-row>

				</fo:table-body>
			</fo:table>
		</fo:block>

	</xsl:template>

	<xsl:template name="footer">
		<fo:block text-align="center">
			Pagina <fo:page-number/>
		</fo:block>
	</xsl:template>

	<xsl:template name="body">
		<fo:block font-weight="bold">
			The following error occurred:
		</fo:block>
		<fo:block linefeed-treatment="preserve">
			<xsl:value-of select="/"/>
		</fo:block>
	</xsl:template>

</xsl:stylesheet>