package it.plansoft.ecommerce.order;

import com.jtok.spring.domainevent.DomainEvent;
import it.plansoft.ecommerce.ECommerceDomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;

@Entity
@Table(name = "Orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;
    @NotNull
    String globalId;
    @NotNull
    String customer;
    @NotNull
    OrderStatus status;
    @NotNull
    Currency currency;
    @NotNull
    BigDecimal granTotal;
    @Nullable
    String notes;

    /**
     * Clears all domain events currently held. Usually invoked by the infrastructure in place in Spring Data
     * repositories.
     */
    @AfterDomainEventPublication
    protected void clearDomainEvents() {
    }

    /**
     * All domain events currently captured by the aggregate.
     */
    @DomainEvents
    protected Collection<Object> domainEvents() {

        if (this.getStatus() == OrderStatus.TO_BE_PAYED) { ;

            final Order obj = this;

            DomainEvent event = DomainEvent.builder()
                    .key(this.getGlobalId())
                    .domainEventType(ECommerceDomainEvent.ORDER_TO_BE_PAYED)
                    .applicationPayload(new HashMap<String, Object>() {{
                        put("globalId", obj.getGlobalId());
                        put("customer", obj.getCustomer());
                        put("currency", obj.getCurrency().getCurrencyCode());
                        put("granTotal", obj.getGranTotal());
                    }})
                    .build();

            return Collections.singletonList(event);
        } else {
            return Collections.emptyList();
        }
    }

}
