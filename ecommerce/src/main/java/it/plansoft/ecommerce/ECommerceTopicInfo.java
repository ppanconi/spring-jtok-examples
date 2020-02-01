package it.plansoft.ecommerce;

import com.jtok.spring.domainevent.DomainEventTopicInfo;

public enum  ECommerceTopicInfo implements DomainEventTopicInfo {

    ORDER_CREATED_TOPIC("order_created", 5, 3),
    ORDER_TO_BE_PAYED_TOPIC("order_to_be_payed", 5, 3);

    private String topicName ;
    private int topicPartitions;
    private int topicReplications;

    ECommerceTopicInfo(String topicName, int topicPartitions, int topicReplications) {
        this.topicName = topicName;
        this.topicPartitions = topicPartitions;
        this.topicReplications = topicReplications;
    }

    @Override
    public String topicName() {
        return topicName;
    }

    @Override
    public int topicPartitions() {
        return topicPartitions;
    }

    @Override
    public int topicReplications() {
        return topicReplications;
    }
}
