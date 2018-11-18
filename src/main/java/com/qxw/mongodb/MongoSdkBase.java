package com.qxw.mongodb;
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;

/**
 * 	mongodb工具类
 * @author qinxuewu
 *
 */
public class MongoSdkBase {
    /**
     * 表连接
     */
    public static MongoCollection<Document> getColl(String tableName) {
        MongoDatabase db = MongoFactory.getMongoDb("testdb");
        return db.getCollection(tableName);
    }
    public static MongoCollection<Document> getColl(String tableName,String dbName) {
        MongoDatabase db = MongoFactory.getMongoDb(dbName);
        return db.getCollection(tableName);
    }

    /***
     * 插入单条记录
     * @param table  表连接
     * @param obj Document
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String insertOneDocument(MongoCollection table, Document doc) {
        if (doc == null) return null;
        doc.remove("_id");
        doc.put("_id", new ObjectId().toString());
        table.insertOne(doc);
        return doc.get("_id").toString();
    }

    /***
     * 插入单条记录
     * @param table  表连接
     * @param obj 单条数据
     * obj double 处理不规范
     * @return
     */
    @SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	public static String insertOne(MongoCollection table, Object obj) {
        if (obj == null) return null;
        Document docine = new Document().parse(diyObjectIdToJson(obj));
        docine.remove("_id");
        docine.put("_id", new ObjectId().toString());
        table.insertOne(docine);
        return docine.get("_id").toString();
    }

    /**
     * 删除找到的第一条记录
     *
     * @param table  表连接
     * @param filter
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static int deleteOne(MongoCollection table, String _id) {
        Bson filter = eq("_id", _id);
        DeleteResult re = table.deleteOne(filter);
        return (int) re.getDeletedCount();
    }

    /**
     * 根据条件更新单条记录
     *
     * @param table  表连接
     * @param filter
     * @param obj
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static boolean updateOne(MongoCollection table, String _id, Object obj) {
        Bson filter = eq("_id", _id);
        table.updateOne(filter, _set(diyObjectIdToJson(obj)));
        return true;
    }

    /**
     * 查询 单条记录 返回json字符串
     *
     * @param table  表连接
     * @param filter
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static String seleteOne(MongoCollection table, String _id) {
        Bson filter = eq("_id", _id);
        return diyObjectIdToJson(seleteOneDocument(table, filter));
    }

    /**
     * 查询 单条记录 返回json字符串
     *
     * @param table  表连接
     * @param filter
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static String seleteOne(MongoCollection table, Bson filter) {
        return diyObjectIdToJson(seleteOneDocument(table, filter));
    }

    /**
     * 查询 单条记录 返回 org.bson.Document  对象
     *
     * @param table  表连接
     * @param filter
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static Document seleteOneDocument(MongoCollection table, Bson filter) {
        FindIterable<Document> result = table.find(filter);
        return result.first();
    }

    /**
     * 查询所有记录  代码控制返回结果数
     *
     * @param table  表连接
     * @param filter 条件  com.mongodb.client.model.Filter
     * @param sort   排序    com.mongodb.client.model.Sorts   可空
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<JSONObject> getAll(MongoCollection table, Bson filter, Bson sort) {
        List<JSONObject> list = new ArrayList<JSONObject>();
        FindIterable<Document> result = null;
        if (filter == null) {
            result = table.find().sort(sort);
        } else {
            result = table.find(filter).sort(sort);
        }

        MongoCursor<Document> iterator = result.iterator();

        while (iterator.hasNext()) {
            Object ddd = iterator.next();
            list.add(JSON.parseObject(diyObjectIdToJson(ddd)));
        }
        return list;
    }

    @SuppressWarnings("rawtypes")
	public static List<JSONObject> getAll_byCid(MongoCollection table, int cid) {
        return getAll(table, eq("cid", cid), null);
    }

    /**
     * 分页查询
     *
     * @param table    表连接
     * @param filter   条件  com.mongodb.client.model.Filter
     * @param sort     排序    com.mongodb.client.model.Sorts
     *                 //ascending(orderBy); //descending("");
     * @param pageNum
     * @param pageSize
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONObject getPage(MongoCollection table, Bson filter, Bson sort, int pageNum, int pageSize) {

        if (pageNum == 0) pageNum = 1;
        if (pageSize == 0) pageSize = 10;
        int totalCount = (int) (filter == null ? table.count() : table.count(filter));//table.count(filter);
        int totalPage = (int) (totalCount / pageSize + ((totalCount % pageSize == 0) ? 0 : 1));
        if (pageNum > totalPage) pageNum = totalPage;

        JSONObject msg = new JSONObject();
        msg.put("pageNum", pageNum);
        msg.put("pageSize", pageSize);
        msg.put("totalCount", totalCount);
        msg.put("totalPage", totalPage);
        List<JSONObject> list = new ArrayList<JSONObject>();
        if (totalCount > 0) {
            int startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;
            FindIterable<Document> result = null;//filter==null?(table.find().skip(startRow).limit(totalPage)):(table.find(filter).skip(startRow).limit(totalPage));// table.find(filter);
            if (filter == null) {
                result = table.find().sort(sort).skip(startRow).limit(pageSize);
            } else {
                result = table.find(filter).sort(sort).skip(startRow).limit(pageSize);
            }
            MongoCursor<Document> iterator = result.iterator();
            while (iterator.hasNext()) {
                Document ddd = (Document) iterator.next();
                list.add(JSON.parseObject(diyObjectIdToJson(ddd)));
            }
        }
        msg.put("data", list);
        return msg;
    }


    /**
     * 分组分页查询
     *
     * @param table    表连接
     * @param filter   条件  com.mongodb.client.model.Filter
     * @param sort     排序    com.mongodb.client.model.Sorts
     *                 //ascending(orderBy); //descending("");
     * @param pageNum
     * @param pageSize
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public static JSONObject getGroupPage(MongoCollection table, Bson filter, Bson group, Bson sorts, int pageNum, int pageSize) {

        if (pageNum == 0) pageNum = 1;
        if (pageSize == 0) pageSize = 10;

        List<JSONObject> list = new ArrayList<>();
        int startRow = pageNum > 0 ? (pageNum - 1) * pageSize : 0;

        MongoCursor<Document> iterator = table.aggregate(Arrays.asList(match(filter), group, sort(sorts), skip(startRow), limit(pageSize))).iterator();

        while (iterator.hasNext()) {
            Document doc = iterator.next();
            list.add(JSON.parseObject(diyObjectIdToJson(doc)));
        }

        int totalCount = list != null && list.size() > 0 ? list.size() : 0;
        int totalPage = (int) (totalCount / pageSize + ((totalCount % pageSize == 0) ? 0 : 1));

        JSONObject msg = new JSONObject();
        msg.put("pageNum", pageNum);
        msg.put("pageSize", pageSize);
        msg.put("totalCount", totalCount);
        msg.put("totalPage", totalPage);
        msg.put("data", list);

        return msg;
    }


    /******************************** ***********************************/


    /******************************以下为工具*************************************/
    /**
     * 获得自增id 返回一个可用最大id
     *
     * @param type 类型 必须
     */
    public static int getAutoIncId(String type) {
        return getAutoIncId(type, 0);
    }

    /**
     * 获得自增id 返回一个可用最大id
     *
     * @param type 类型 必须
     * @param def  初始值
     * @return
     */
    @SuppressWarnings("rawtypes")
	public static int getAutoIncId(String type, int def) {
        if (type == null || "".equals(type)) return 0;
        MongoDatabase db = MongoFactory.getMongoDb();
        MongoCollection table = db.getCollection("ids");
        Bson filter = and(eq("type", type));//
        Document oo = (Document) table.findOneAndUpdate(filter,
                combine(inc("id", 1), set("date", new Date()), setOnInsert("_id", new ObjectId().toString())),
                new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
        int id = oo.getInteger("id");
        if (id < def) {
            Document oo2 = (Document) table.findOneAndUpdate(filter, inc("id", def),
                    new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER));
            return oo2.getInteger("id");
        }
        return id;
    }

    /**
     * 更新数据 注意 多余字段 会在库表记录追加
     *
     * @param json
     * @return
     */
    @SuppressWarnings("static-access")
	public static Document _set(String json) {
        Document b = new Document().parse(json);//	b.put(fieldName, value);
        b.remove("_id");
        return new Document("$set", b);
    }

    private static SerializeFilter objectIdSerializer = new ValueFilter() {
        @Override
        public Object process(Object object, String name, Object value) {
            if ("_id".equals(name)) {
                if (value instanceof ObjectId) {
                    return value.toString();
                }
            }
            return value;
        }
    };

    /**
     * 出库后查询
     *
     * @param object
     * @return
     */
    public static final String diyObjectIdToJson(Object object) {
        return JSON.toJSONString(object, objectIdSerializer,
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.WriteNullNumberAsZero,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty);
    }

    /**
     * 出库后查询 null跳过不转换为json
     *
     * @param object
     * @return
     */
    public static final String diyObjToJson(Object object) {
        return JSON.toJSONString(object, objectIdSerializer,
                SerializerFeature.WriteDateUseDateFormat);
    }
}











