package com.example.services;

import com.example.MongoStore;
import com.mongodb.client.model.Sorts;
import java.util.Date;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
public class ProductoService {
    @GET @SuppressWarnings("unchecked")
    public Response getAll() {
        try {
            JSONArray array = new JSONArray();
            for (Document d : MongoStore.productos().find().sort(Sorts.ascending("NOMBRE"))) {
                JSONObject p = new JSONObject();
                Object id = d.get("_id");
                p.put("id", id instanceof ObjectId ? ((ObjectId)id).toHexString() : String.valueOf(id));
                p.put("nombre", d.get("NOMBRE")); p.put("marca", d.get("MARCA"));
                Object date = d.get("FECHACOMPRA");
                p.put("fechaCompra", date instanceof Date ? ((Date)date).toInstant().toString() : date);
                array.add(p);
            }
            return Response.ok(array.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
        } catch (Exception ex) {
            ex.printStackTrace(); JSONObject error = new JSONObject();
            error.put("error", ex.getClass().getSimpleName()); error.put("detalle", String.valueOf(ex.getMessage()));
            return Response.status(500).entity(error.toJSONString()).build();
        }
    }
}
