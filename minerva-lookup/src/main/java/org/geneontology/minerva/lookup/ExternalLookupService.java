package org.geneontology.minerva.lookup;

import java.util.List;

import org.semanticweb.owlapi.model.IRI;

/**
 * Interface for wrapping a service to lookup information for a given identifier.
 */
public interface ExternalLookupService {

	/**
	 * Result of an id lookup.
	 */
	public static class LookupEntry {

		public final IRI id;
		public final String label;
		public final String type;
		public final String taxon;
		public final List<String> isa_closure;
		public final String direct_parent_iri;
		
		/**
		 * @param id
		 * @param label
		 * @param type
		 * @param taxon
		 */
		public LookupEntry(IRI id, String label, String type, String taxon, List<String> isa_closure) {
			this.id = id;
			this.label = label;
			this.type = type;
			this.taxon = taxon;
			this.isa_closure = isa_closure;
			if(isa_closure!=null) {
				if(isa_closure.contains("CHEBI:36080")) {
					//protein
					direct_parent_iri = "http://purl.obolibrary.org/obo/CHEBI_36080";
				}else if(isa_closure.contains("CHEBI:33695")) {
					//information biomacrolecule (gene, complex)
					direct_parent_iri = "http://purl.obolibrary.org/obo/CHEBI_33695";
				}else {
					direct_parent_iri = null;
				}
			}else {
				direct_parent_iri = null;
			}
		}
	}
	
	/**
	 * Lookup the information for the given identifier. This is not a search.
	 * 
	 * @param id
	 * @return entries
	 */
	public List<LookupEntry> lookup(IRI id);
	
	/**
	 * Lookup the information for the given identifier and taxon. This is not a
	 * search.
	 * 
	 * @param id
	 * @param taxon
	 * @return entry
	 */
	public LookupEntry lookup(IRI id, String taxon);
	
}
