package com.elk.services;


import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.elk.Utils.Utils;
import com.elk.exception.NotSavedException;
import com.elk.mappers.ProductMapper;
import com.elk.model.Product;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped
public class ProductService {

    Logger logger = Logger.getLogger(this.getClass().getName());
    @Inject
    private SqlSession session;

    @Inject
    private ElasticsearchClient elasticsearchClient;
    @Inject
    private ElasticsearchAsyncClient elasticsearchAsyncClient;
    private static final String PRODUCT_INDEX = "index_products";

    public long saveProduct(Product product) throws Exception {
        long uniqueId = Utils.uniqueCurrentTimeNS();
        product.setId(uniqueId);

        CompletableFuture<Boolean> saveInPrimaryDb = saveInPrimaryDb(product)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving the product in mysql db");
                    return false;
                });

        CompletableFuture<Boolean> saveInSecondaryDb = saveInSecondaryDb(product);

        CompletableFuture.allOf(saveInPrimaryDb, saveInSecondaryDb).join();
        if (saveInSecondaryDb.get() && saveInSecondaryDb.get()) {
            session.commit();
            return uniqueId;
        }
        rollBackOperation(session, uniqueId);
        throw new NotSavedException("Unable to save product");

    }

    public CompletableFuture<Boolean> saveInPrimaryDb(Product product) {
        return CompletableFuture
                .supplyAsync(() -> {
                            ProductMapper productMapper = session.getMapper(ProductMapper.class);
                            productMapper.saveProduct(product);
                            return true;
                        }
                ).exceptionally(throwable -> {
                    throw new RuntimeException();
                });
    }

    public CompletableFuture<Boolean> saveInSecondaryDb(Product product) {

        return CompletableFuture
                .supplyAsync(() -> {
                    try {
                        elasticsearchClient.index(data -> data
                                .index(PRODUCT_INDEX)
                                .id(String.valueOf(product.getId()))
                                .document(product)
                        );
                    } catch (IOException e) {
                        logger.log(Level.INFO, "Error while saving data in elastic search");
                        return false;
                    }
                    return true;
                }).exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving data in elastic search");
                    return false;
                });
    }

    private void rollBackOperation(SqlSession session, Long uniqueId) {
        session.rollback();
        elasticsearchAsyncClient.delete(builder -> builder.index(PRODUCT_INDEX)
                .id(String.valueOf(uniqueId)));
    }

    public Long updateOperation(Product product) throws Exception {
        CompletableFuture<Boolean> updateInPrimaryDb = updateInPrimaryDb(product)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving the product in mysql db");
                    return false;
                });

        CompletableFuture<Boolean> updateInSecondaryDb = updateInSecondaryDb(product)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving data in elastic search");
                    return false;
                });

        CompletableFuture.allOf(updateInPrimaryDb, updateInSecondaryDb).join();
        if (updateInSecondaryDb.get() && updateInSecondaryDb.get()) {
            session.commit();
            return product.getId();
        }
        rollBackOperation(session, product.getId());
        throw new NotSavedException("Unable to save product");

    }

    public CompletableFuture<Boolean> updateInPrimaryDb(Product product) {
        return CompletableFuture
                .supplyAsync(() -> {
                            ProductMapper productMapper = session.getMapper(ProductMapper.class);
                            productMapper.updateProduct(product);
                            return true;
                        }
                ).exceptionally(throwable -> {
                    throw new RuntimeException();
                });
    }

    public CompletableFuture<Boolean> updateInSecondaryDb(Product product) {

        return CompletableFuture
                .supplyAsync(() -> {
                            try {
                                elasticsearchClient.update(u -> u.index(PRODUCT_INDEX)
                                        .id(String.valueOf(product.getId()))
                                        .upsert(product)
                                        .doc(product), Product.class);
                                return true;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving data in elastic search");
                    throw new RuntimeException();
                });
    }


    public List<Product> getProducts() throws IOException {
        List<Product> collect = List.of();
        try {
            SearchResponse<Product> search = elasticsearchClient.search(s -> s
                            .index(PRODUCT_INDEX)
                            .query(q -> q
                                    .matchAll(m -> m.queryName("{}"))
                            ),
                    Product.class
            );
            List<Hit<Product>> allHits = search.hits().hits();
            collect = allHits.stream().map(Hit::source).collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace();
            logger.log(Level.INFO, "Failed to query the Products");
        }
        return collect;
    }

    public List<Product> getProductByName(String name) throws IOException {
        List<Product> products = List.of();
        try {
            SearchResponse<Product> allMatches = elasticsearchClient.search(s -> s
                            .index(PRODUCT_INDEX)
                            .query(q -> q
                                    .match(t -> t
                                            .field("name")
                                            .query(name)
                                    )
                            ),
                    Product.class
            );
            products = allMatches.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Exception while getting products with name: " + name);
        }
        return products;
    }

    public long deleteById(Long id) throws Exception {

        CompletableFuture<Boolean> deleteInPrimaryDb = deleteInPrimaryDb(id)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving the product in mysql db");
                    return false;
                });

        CompletableFuture<Boolean> deleteInSecondaryDb = deleteInSecondaryDb(id)
                .exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving data in elastic search");
                    return false;
                });

        CompletableFuture.allOf(deleteInPrimaryDb, deleteInSecondaryDb).join();
        if (deleteInPrimaryDb.get() && deleteInSecondaryDb.get()) {
            session.commit();
            return id;
        }
        rollBackOperation(session, id);
        throw new NotSavedException("Unable to delete product");

    }

    public CompletableFuture<Boolean> deleteInPrimaryDb(Long id) {
        return CompletableFuture
                .supplyAsync(() -> {
                            ProductMapper productMapper = session.getMapper(ProductMapper.class);
                            productMapper.deleteProduct(id);
                            return true;
                        }
                ).exceptionally(throwable -> {
                    throw new RuntimeException();
                });
    }

    public CompletableFuture<Boolean> deleteInSecondaryDb(Long id) {

        return CompletableFuture
                .supplyAsync(() -> {
                            try {
                                elasticsearchClient.delete(data -> data
                                        .index(PRODUCT_INDEX)
                                        .id(String.valueOf(id))
                                );
                                return true;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                ).exceptionally(throwable -> {
                    logger.log(Level.INFO, "Error while saving data in elastic search");
                    throw new RuntimeException();
                });
    }

}
