package com.therapy.nest.shared.repository;

import com.therapy.nest.shared.entity.RabbitMQMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RabbitMQMessageRepository extends JpaRepository<RabbitMQMessage, Long>, JpaSpecificationExecutor<RabbitMQMessage> {
}
