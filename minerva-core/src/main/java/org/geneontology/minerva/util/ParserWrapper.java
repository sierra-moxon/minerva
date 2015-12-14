package org.geneontology.minerva.util;

import java.io.IOException;
import owltools.io.CatalogXmlIRIMapper;

public class ParserWrapper extends owltools.io.ParserWrapper {
	public ParserWrapper() throws IOException
	{
		super();

        addIRIMapper(new CatalogXmlIRIMapper("../cache/catalog-v001.xml"));
	}
}
