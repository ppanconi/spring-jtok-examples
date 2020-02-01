package it.plansoft.payments.account;

import com.jtok.spring.subscriber.ExternalDomainEvent;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public int addCharge(String userId, BigDecimal amount,
                         String refType, String refId, String refKey) {

        Optional<Account> byUserId = repository.findByUserId(userId);
        if ( byUserId.isPresent()) {
            Account account = byUserId.get();
            int added = account.charge(amount, refType, refId, refKey);
            repository.save(account);
            return added;
        } else {
            return -10;
        }
    }

    @Transactional
    public int addDeposit(String userId, BigDecimal amount,
                          String refType, String refId, String refKey) {

        Optional<Account> byUserId = repository.findByUserId(userId);
        if ( byUserId.isPresent()) {
            Account account = byUserId.get();
            int added = account.deposit(amount, refType, refId, refKey);
            repository.save(account);
            return added;
        } else {
            return -10;
        }
    }

    public Optional<Account> getByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Transactional
    @EventListener(condition = "#event.name == 'ecommerce.ORDER_TO_BE_PAYED'")
    public void handleECommerceOrderCreated(ExternalDomainEvent event) {

        JSONObject payload = event.getPayload();
        String userId = payload.getAsString("customer");
        String orderId = payload.getAsString("globalId");

        Number granTotal = payload.getAsNumber("granTotal");

        BigDecimal charge = new BigDecimal(granTotal.doubleValue()).multiply(new BigDecimal("-1.00"));

        int status = addCharge(userId, charge, event.getName(), event.getId(), orderId);

        if (status == -10) {
            //TODO rise event
            log.warn("User " + userId + " not found on payments database");
        }

    }

}