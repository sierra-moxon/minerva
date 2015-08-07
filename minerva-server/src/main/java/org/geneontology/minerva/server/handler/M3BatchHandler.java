package org.geneontology.minerva.server.handler;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.geneontology.minerva.json.JsonAnnotation;
import org.geneontology.minerva.json.JsonEvidenceInfo;
import org.geneontology.minerva.json.JsonOwlFact;
import org.geneontology.minerva.json.JsonOwlIndividual;
import org.geneontology.minerva.json.JsonOwlObject;
import org.geneontology.minerva.json.JsonRelationInfo;

import com.google.gson.annotations.SerializedName;

@Path("/")
public interface M3BatchHandler {

	public static class M3Request {
		Entity entity;
		Operation operation;
		M3Argument arguments;
	}
	
	public static enum Entity {
		individual,
		edge,
		model,
		meta;
	}
	
	public static enum Operation {
		// generic operations
		get,
		
		@SerializedName("add-type")
		addType,
		
		@SerializedName("remove-type")
		removeType,
		
		add,
		
		remove,
		
		@SerializedName("add-annotation")
		addAnnotation,
		
		@SerializedName("remove-annotation")
		removeAnnotation,
		
		// model specific operations
		@SerializedName("export")
		exportModel,
		
		@SerializedName("export-legacy")
		exportModelLegacy,
		
		@SerializedName("import")
		importModel,
		
		@SerializedName("store")
		storeModel,
		
		@SerializedName("update-imports")
		updateImports,
		
		// undo operations for models
		undo, // undo the latest op
		redo, // redo the latest undo
		@SerializedName("get-undo-redo")
		getUndoRedo, // get a list of all currently available undo and redo for a model
		
	}
	
	public static class M3Argument {
		
		 @SerializedName("model-id")
		String modelId;
		String subject;
		String object;
		String predicate;
		String individual;
		
		@SerializedName("individual-iri")
		String individualIRI;
		
		@SerializedName("taxon-id")
		String taxonId;
		
		@SerializedName("import-model")
		String importModel;
		String format;
		
		@SerializedName("assign-to-variable")
		String assignToVariable;
		
		JsonOwlObject[] expressions;
		JsonAnnotation[] values;
	}
	
	public static class M3BatchResponse {
		@SerializedName("packet-id")
		final String packetId; // generated or pass-through
		final String uid; // pass-through
		/*
		 * pass-through; model:
		 * "query", "action" //, "location"
		 */
		final String intention;
		
		public static final String SIGNAL_MERGE = "merge";
		public static final String SIGNAL_REBUILD = "rebuild";
		public static final String SIGNAL_META = "meta";
		/*
		 * "merge", "rebuild", "meta" //, "location"?
		 */
		String signal;
		
		public static final String MESSAGE_TYPE_SUCCESS = "success";
		public static final String MESSAGE_TYPE_ERROR = "error";
		/*
		 * "error", "success", //"warning"
		 */
		@SerializedName("message-type")
		String messageType;
		/*
		 * "e.g.: server done borked"
		 */
		String message;
		/*
		 * Now degraded to just a String, not an Object.
		 */
		//Map<String, Object> commentary = null;
		String commentary;
		
		ResponseData data;
		
		public static class ResponseData {
			public String id;
			
			@SerializedName("inconsistent-p")
			public Boolean inconsistentFlag;
			
			@SerializedName("modified-p")
			public Boolean modifiedFlag;
			
			public JsonAnnotation[] annotations;
			
			public JsonOwlFact[] facts;
			
			public JsonOwlIndividual[] individuals;
			
			public JsonOwlObject[] properties;
			
			public Object undo;
			public Object redo;
			
			@SerializedName("export-model")
			public String exportModel;
			
			public MetaResponse meta;
		}
		
		public static class MetaResponse {
			public JsonRelationInfo[] relations;
			
			public JsonRelationInfo[] dataProperties;
			
			public JsonEvidenceInfo[] evidence;
			
			@SerializedName("models-meta")
			public Map<String,List<JsonAnnotation>> modelsMeta;
			
			@SerializedName("models-meta-read-only")
			public Map<String, Map<String,Object>> modelsReadOnly;
		}

		/**
		 * @param uid
		 * @param intention
		 * @param packetId
		 */
		public M3BatchResponse(String uid, String intention, String packetId) {
			this.uid = uid;
			this.intention = intention;
			this.packetId = packetId;
		}
		
	}
	
	
	/**
	 * Process a batch request. The parameters uid and intention are round-tripped for the JSONP.
	 * 
	 * @param uid user id, JSONP relevant
	 * @param intention JSONP relevant
	 * @param packetId response relevant, may be null
	 * @param requests batch request
	 * @param isPrivileged true, if the access is privileged
	 * @return response object, never null
	 */
	public M3BatchResponse m3Batch(String uid, String intention, String packetId, M3Request[] requests, boolean isPrivileged);
	
	/**
	 * Jersey REST method for POST with three form parameters.
	 * 
	 * @param intention JSONP relevant
	 * @param packetId
	 * @param requests JSON string of the batch request
	 * @return response convertible to JSON(P)
	 */
	@Path("m3Batch")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public M3BatchResponse m3BatchPost(
			@FormParam("intention") String intention,
			@FormParam("packet-id") String packetId,
			@FormParam("requests") String requests);
	
	/**
	 * Jersey REST method for POST with three form parameters with privileged rights.
	 * 
	 * @param uid user id, JSONP relevant
	 * @param intention JSONP relevant
	 * @param packetId
	 * @param requests JSON string of the batch request
	 * @return response convertible to JSON(P)
	 */
	@Path("m3BatchPrivileged")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public M3BatchResponse m3BatchPostPrivileged(
			@FormParam("uid") String uid,
			@FormParam("intention") String intention,
			@FormParam("packet-id") String packetId,
			@FormParam("requests") String requests);
	
	
	/**
	 * Jersey REST method for GET with three query parameters.
	 * 
	 * @param intention JSONP relevant
	 * @param packetId 
	 * @param requests JSON string of the batch request
	 * @return response convertible to JSON(P)
	 */
	@Path("m3Batch")
	@GET
	public M3BatchResponse m3BatchGet(
			@QueryParam("intention") String intention,
			@QueryParam("packet-id") String packetId,
			@QueryParam("requests") String requests);
	
	/**
	 * Jersey REST method for GET with three query parameters with privileged rights.
	 * 
	 * @param uid user id, JSONP relevant
	 * @param intention JSONP relevant
	 * @param packetId 
	 * @param requests JSON string of the batch request
	 * @return response convertible to JSON(P)
	 */
	@Path("m3BatchPrivileged")
	@GET
	public M3BatchResponse m3BatchGetPrivileged(
			@QueryParam("uid") String uid,
			@QueryParam("intention") String intention,
			@QueryParam("packet-id") String packetId,
			@QueryParam("requests") String requests);
}
