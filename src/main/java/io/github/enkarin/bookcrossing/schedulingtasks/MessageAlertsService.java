package io.github.enkarin.bookcrossing.schedulingtasks;

import io.github.enkarin.bookcrossing.chat.model.Message;
import io.github.enkarin.bookcrossing.chat.repository.MessageRepository;
import io.github.enkarin.bookcrossing.mail.service.MailService;
import io.github.enkarin.bookcrossing.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageAlertsService {

    private final MessageRepository messageRepository;
    private final MailService mailService;

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void sendAlerts() {
        final Map<User, Integer> map = new HashMap<>();
        final List<Message> unread = messageRepository.findAll().stream()
                .peek(m -> log.info("For messageId {} isDeclaim = {} isAlertSent = {}", m.getMessageId(), m.isDeclaim(), m.isAlertSent()))
                .filter(Predicate.not(Message::isDeclaim))
                .filter(Predicate.not(Message::isAlertSent))
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getFirstUser().isAccountNonLocked())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getSecondUser().isAccountNonLocked())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getFirstUser().isEnabled())
                .filter(m -> m.getCorrespondence().getUsersCorrKey().getSecondUser().isEnabled())
                .toList();
        if (!unread.isEmpty()) {
            User user;
            for (final Message message : unread) {
                user = message.getCorrespondence().getUsersCorrKey().getFirstUser();
                if (user.equals(message.getSender())) {
                    user = message.getCorrespondence().getUsersCorrKey().getSecondUser();
                    if (!user.equals(message.getSender())) {
                        map.putIfAbsent(user, 0);
                        map.put(user, map.get(user) + 1);
                    }
                } else {
                    map.putIfAbsent(user, 0);
                    map.put(user, map.get(user) + 1);
                }
            }
            for (final Map.Entry<User, Integer> entry : map.entrySet()) {
                log.info("Sending alert for userId {}", entry.getKey().getUserId());
                mailService.sendAlertsMessage(entry.getKey(), entry.getValue());
            }
            unread.forEach(m -> {
                m.setAlertSent(true);
                messageRepository.save(m);
            });
        }
    }
}
