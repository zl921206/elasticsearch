package com.kamluen.elasticsearch.dao.impl;

import org.springframework.stereotype.Repository;

/**
 * @author zhanglei
 * 股票基本信息dao接口实现类
 * @date 2018-11-01
 */
@Repository
public class StockInfoDaoImpl {

//    private final String INDEX = "bookdata";
//    private final String TYPE = "books";
//
//    private RestHighLevelClient restHighLevelClient;
//
//    private ObjectMapper objectMapper;
//
//    public StockInfoDaoImpl(ObjectMapper objectMapper, RestHighLevelClient restHighLevelClient) {
//        this.objectMapper = objectMapper;
//        this.restHighLevelClient = restHighLevelClient;
//    }
//
////    @Override
//    public Book insertBook(Book book){
//        book.setId(UUID.randomUUID().toString());
//        Map<String, Object> dataMap = objectMapper.convertValue(book, Map.class);
//        IndexRequest indexRequest = new IndexRequest(INDEX, TYPE, book.getId())
//                .source(dataMap);
//        try {
//            IndexResponse response = restHighLevelClient.index(indexRequest);
//        } catch(ElasticsearchException e) {
//            e.getDetailedMessage();
//        } catch (java.io.IOException ex){
//            ex.getLocalizedMessage();
//        }
//        return book;
//    }
//
////    @Override
//    public Map<String, Object> getBookById(String id){
//        GetRequest getRequest = new GetRequest(INDEX, TYPE, id);
//        GetResponse getResponse = null;
//        try {
//            getResponse = restHighLevelClient.get(getRequest);
//        } catch (java.io.IOException e){
//            e.getLocalizedMessage();
//        }
//        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
//        return sourceAsMap;
//    }
//
////    @Override
//    public Map<String, Object> updateBookById(String id, Book book){
//        UpdateRequest updateRequest = new UpdateRequest(INDEX, TYPE, id)
//                .fetchSource(true);    // Fetch Object after its update
//        Map<String, Object> error = new HashMap<>();
//        error.put("Error", "Unable to update book");
//        try {
//            String bookJson = objectMapper.writeValueAsString(book);
//            updateRequest.doc(bookJson, XContentType.JSON);
//            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest);
//            Map<String, Object> sourceAsMap = updateResponse.getGetResult().sourceAsMap();
//            return sourceAsMap;
//        }catch (JsonProcessingException e){
//            e.getMessage();
//        } catch (java.io.IOException e){
//            e.getLocalizedMessage();
//        }
//        return error;
//    }
//
////    @Override
//    public void deleteBookById(String id) {
//        DeleteRequest deleteRequest = new DeleteRequest(INDEX, TYPE, id);
//        try {
//            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest);
//        } catch (java.io.IOException e){
//            e.getLocalizedMessage();
//        }
//    }

}
