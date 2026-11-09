package com.example.services;

import com.example.MongoStore;
import com.example.models.CompetitorDTO;
import com.example.models.Producto;
import com.example.models.Vehicle;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@Path("/competitors")
@Produces(MediaType.APPLICATION_JSON)
public class CompetitorService {
    @GET
    public Response getAll() {
        try {
            JSONArray list = new JSONArray();
            for (Document d : MongoStore.competitors().find().sort(Sorts.ascending("SURNAME"))) list.add(toJson(d));
            return ok(list.toJSONString());
        } catch (Exception ex) { return error(ex); }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCompetitor(CompetitorDTO dto) {
        if (dto == null || dto.getProducto() == null || blank(dto.getProducto().getNombre()) ||
            blank(dto.getProducto().getMarca()) || dto.getProducto().getFechaCompra() == null) {
            return Response.status(400).entity("{\"error\":\"Producto obligatorio: nombre, marca, fechaCompra\"}")
                    .header("Access-Control-Allow-Origin", "*").build();
        }
        try {
            ObjectId productId = new ObjectId();
            ObjectId competitorId = new ObjectId();
            Producto p = dto.getProducto();
            Date date = p.getFechaCompra().getTime();
            Document product = new Document("_id", productId)
                .append("NOMBRE", p.getNombre()).append("MARCA", p.getMarca()).append("FECHACOMPRA", date);
            Document competitor = new Document("_id", competitorId)
                .append("NAME", dto.getName()).append("SURNAME", dto.getSurname())
                .append("AGE", dto.getAge()).append("TELEPHONE", dto.getTelephone())
                .append("CELLPHONE", dto.getCellphone()).append("ADDRESS", dto.getAddress())
                .append("CITY", dto.getCity()).append("COUNTRY", dto.getCountry())
                .append("WINNER", false).append("create_at", new Date()).append("updated_at", new Date())
                .append("PRODUCTO_ID", productId);
            Vehicle v = dto.getVehicle();
            if (v != null) competitor.append("VEHICLE", new Document("PLATE", v.getPlate())
                    .append("BRAND", v.getBrand()).append("MODELYEAR", v.getModelYear()));
            MongoStore.productos().insertOne(product);
            try { MongoStore.competitors().insertOne(competitor); }
            catch (Exception ex) { MongoStore.productos().deleteOne(Filters.eq("_id", productId)); throw ex; }
            JSONObject result = new JSONObject();
            result.put("competitor_id", competitorId.toHexString());
            result.put("producto_id", productId.toHexString());
            return Response.status(201).entity(result.toJSONString())
                .header("Access-Control-Allow-Origin", "*").build();
        } catch (Exception ex) { return error(ex); }
    }

    @GET @Path("/names/a")
    public Response getNamesStartingWithA() {
        try {
            JSONArray names = new JSONArray();
            for (Document d : MongoStore.competitors().find(Filters.regex("NAME", "^A", "i"))) {
                Object value = d.get("NAME"); if (value != null) names.add(value.toString());
            }
            return ok(names.toJSONString());
        } catch (Exception ex) { return error(ex); }
    }

    @GET @Path("/{name}")
    public Response getByName(@PathParam("name") String name) {
        try {
            JSONArray list = new JSONArray();
            for (Document d : MongoStore.competitors().find(Filters.eq("NAME", name))) list.add(toJson(d));
            return ok(list.toJSONString());
        } catch (Exception ex) { return error(ex); }
    }

    @SuppressWarnings("unchecked")
    private static JSONObject toJson(Document doc) {
        JSONObject out = new JSONObject();
        for (String key : doc.keySet()) {
            Object value = doc.get(key);
            if (value instanceof ObjectId) value = ((ObjectId)value).toHexString();
            else if (value instanceof Date) value = ((Date)value).toInstant().toString();
            else if (value instanceof Document) value = toJson((Document)value);
            out.put(key, value);
        }
        return out;
    }
    private static Response ok(String json) { return Response.ok(json)
        .header("Access-Control-Allow-Origin", "*").build(); }
    private static boolean blank(String v) { return v == null || v.trim().isEmpty(); }
    @SuppressWarnings("unchecked")
    private static Response error(Exception ex) {
        ex.printStackTrace(); JSONObject e = new JSONObject();
        e.put("error", ex.getClass().getSimpleName()); e.put("detalle", String.valueOf(ex.getMessage()));
        return Response.status(500).entity(e.toJSONString()).build();
    }
    @OPTIONS public Response cors(@javax.ws.rs.core.Context HttpHeaders headers) {
        return Response.ok().header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
            .header("Access-Control-Allow-Headers", "AUTHORIZATION, content-type, accept").build();
    }
}
