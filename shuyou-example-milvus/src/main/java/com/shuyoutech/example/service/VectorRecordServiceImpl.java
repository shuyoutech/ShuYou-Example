package com.shuyoutech.example.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shuyoutech.common.core.util.HttpClientUtils;
import com.shuyoutech.example.domain.VectorRecordEntity;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.vector.request.*;
import io.milvus.v2.service.vector.request.data.FloatVec;
import io.milvus.v2.service.vector.response.DeleteResp;
import io.milvus.v2.service.vector.response.GetResp;
import io.milvus.v2.service.vector.response.InsertResp;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <a href="https://milvus.io/docs/zh/quickstart.md">Milvus</a>
 *
 * @author YangChao
 * @date 2025-08-13 21:55:39
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class VectorRecordServiceImpl implements VectorRecordService {

    public static final String COLLECTION_NAME = "vector_record";
    public Gson gson = new Gson();

    @Override
    public void createCollection() {
        CreateCollectionReq.CollectionSchema collectionSchema = MilvusClientV2.CreateSchema();
        collectionSchema.addField(AddFieldReq.builder().fieldName("id").dataType(DataType.VarChar).isPrimaryKey(Boolean.TRUE).autoID(Boolean.FALSE).description("id").build());
        collectionSchema.addField(AddFieldReq.builder().fieldName("title").dataType(DataType.VarChar).maxLength(10000).description("标题").build());
        collectionSchema.addField(AddFieldReq.builder().fieldName("vector").dataType(DataType.FloatVector).dimension(1024).description("向量值").build());
        IndexParam indexParam = IndexParam.builder().fieldName("vector").metricType(IndexParam.MetricType.COSINE).build();
        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder() //
                .collectionName(COLLECTION_NAME) //
                .description("向量记录表") //
                .dimension(1024) //
                .collectionSchema(collectionSchema) //
                .indexParams(Collections.singletonList(indexParam)) //
                .build();
        milvusClientV2.createCollection(createCollectionReq);
    }

    @Override
    public void save(VectorRecordEntity entity) {
        JsonObject vector = new JsonObject();
        vector.addProperty("id", entity.getId());
        vector.addProperty("title", entity.getTitle());

        JSONObject body = new JSONObject();
        body.put("model", "text-embedding-v4");
        body.put("input", "中国");
        body.put("dimension", "1024");
        body.put("encoding_format", "float");

        List<Header> headers = CollectionUtil.newArrayList();
        headers.add(new BasicHeader("satoken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpblR5cGUiOiJsb2dpbiIsImxvZ2luSWQiOiIyN2YwMjQ2MGJhYzA0YTJiOTNiNjEyMDM2Yzk4NmRmMyIsInJuU3RyIjoidWJIRTJhSUNGeXM2MkloNm5FQ1FFRUV2ZXgxSkxHcjMiLCJ1c2VyX2lkIjoiMjdmMDI0NjBiYWMwNGEyYjkzYjYxMjAzNmM5ODZkZjMiLCJ1c2VyX25hbWUiOiJZYW5nQ2hhbyIsInVzZXJfdHlwZSI6ImFkbWluIn0.8MRf3A6UmXkTC7-qY_lgnzsZOp0cQw1ZU1gIvmWRa50"));
        HttpPost httpPost = new HttpPost("http://localhost:9001/v1/embeddings");
        httpPost.setEntity(new StringEntity(body.toJSONString(), ContentType.APPLICATION_JSON));
        HttpClientUtils.setHeaders(httpPost, headers);
        String result = HttpClientUtils.execute(httpPost);
        JSONObject object = JSON.parseObject(result);
        JSONArray jsonArray = object.getJSONArray("data");
        List<Float> vectorList = new ArrayList<>();
        for (int i = 0; i < jsonArray.getJSONObject(0).getJSONArray("embedding").size(); i++) {
            vectorList.add(jsonArray.getJSONObject(0).getJSONArray("embedding").getFloat(i));
        }
        vector.add("vector", gson.toJsonTree(vectorList));
        InsertReq insertReq = InsertReq.builder().collectionName(COLLECTION_NAME).data(Collections.singletonList(vector)).build();
        InsertResp resp = milvusClientV2.insert(insertReq);
        log.info("save =================== InsertResp:{}", JSON.toJSONString(resp));
    }

    @Override
    public void update(VectorRecordEntity entity) {
        JsonObject vector = new JsonObject();
        vector.addProperty("id", entity.getId());
        vector.addProperty("title", entity.getTitle());
        List<Float> vectorList = new ArrayList<>();
        vectorList.add(2.0f);
        vectorList.add(3.0f);
        vector.add("vector", gson.toJsonTree(vectorList));
        UpsertReq upsertReq = UpsertReq.builder().collectionName(COLLECTION_NAME).data(Collections.singletonList(vector)).build();
        milvusClientV2.upsert(upsertReq);
    }

    @Override
    public void get(String id) {
        GetReq getReq = GetReq.builder().collectionName(COLLECTION_NAME).ids(Collections.singletonList(id)).build();
        GetResp resp = milvusClientV2.get(getReq);
        log.info("get =================== GetResp:{}", JSON.toJSONString(resp));
    }

    @Override
    public void delete(String id) {
        DeleteReq deleteReq = DeleteReq.builder().collectionName(COLLECTION_NAME).ids(Collections.singletonList(id)).build();
        DeleteResp deleteResp = milvusClientV2.delete(deleteReq);
        log.info("delete =================== DeleteResp:{}", JSON.toJSONString(deleteResp));
    }

    @Override
    public void search(String keyword) {
        JSONObject body = new JSONObject();
        body.put("model", "text-embedding-v4");
        body.put("input", keyword);
        body.put("dimension", "1024");
        body.put("encoding_format", "float");

        List<Header> headers = CollectionUtil.newArrayList();
        headers.add(new BasicHeader("satoken", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpblR5cGUiOiJsb2dpbiIsImxvZ2luSWQiOiIyN2YwMjQ2MGJhYzA0YTJiOTNiNjEyMDM2Yzk4NmRmMyIsInJuU3RyIjoidWJIRTJhSUNGeXM2MkloNm5FQ1FFRUV2ZXgxSkxHcjMiLCJ1c2VyX2lkIjoiMjdmMDI0NjBiYWMwNGEyYjkzYjYxMjAzNmM5ODZkZjMiLCJ1c2VyX25hbWUiOiJZYW5nQ2hhbyIsInVzZXJfdHlwZSI6ImFkbWluIn0.8MRf3A6UmXkTC7-qY_lgnzsZOp0cQw1ZU1gIvmWRa50"));
        HttpPost httpPost = new HttpPost("http://localhost:9001/v1/embeddings");
        httpPost.setEntity(new StringEntity(body.toJSONString(), ContentType.APPLICATION_JSON));
        HttpClientUtils.setHeaders(httpPost, headers);
        String resultStr = HttpClientUtils.execute(httpPost);
        JSONObject object = JSON.parseObject(resultStr);
        JSONArray jsonArray = object.getJSONArray("data");
        List<Float> vectorList = new ArrayList<>();
        for (int i = 0; i < jsonArray.getJSONObject(0).getJSONArray("embedding").size(); i++) {
            vectorList.add(jsonArray.getJSONObject(0).getJSONArray("embedding").getFloat(i));
        }

        SearchResp searchR = milvusClientV2.search(SearchReq.builder() //
                .collectionName(COLLECTION_NAME) //
                .data(Collections.singletonList(new FloatVec(vectorList))) //
                .topK(10) //
                .outputFields(Collections.singletonList("*"))//
                .build());
        List<List<SearchResp.SearchResult>> searchResults = searchR.getSearchResults();
        for (List<SearchResp.SearchResult> results : searchResults) {
            for (SearchResp.SearchResult result : results) {
                log.info("search ================ ID={},Score={},Result={}", result.getId(), result.getScore(), result.getEntity().toString());
            }
        }
    }

    private final MilvusClientV2 milvusClientV2;

}