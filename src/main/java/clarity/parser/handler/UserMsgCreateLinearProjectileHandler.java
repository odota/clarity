package clarity.parser.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clarity.match.Match;
import clarity.parser.Handler;
import clarity.parser.HandlerHelper;
import clarity.parser.RegisterHandler;

import com.dota2.proto.DotaUsermessages.CDOTAUserMsg_CreateLinearProjectile;

@RegisterHandler(CDOTAUserMsg_CreateLinearProjectile.class)
public class UserMsgCreateLinearProjectileHandler implements Handler<CDOTAUserMsg_CreateLinearProjectile> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public void apply(int peekTick, CDOTAUserMsg_CreateLinearProjectile message, Match match) {
        HandlerHelper.traceMessage(log, peekTick, message);
        log.debug("{} CREATE_LINEAR_PROJECTILE [handle={}, origin={}, type={}]",
            match.getReplayTimeAsString(),
            message.getHandle(),
            match.getEntities().getByIndex(message.getEntindex()).getDtClass().getDtName(), 
            match.getStringTables().forName("ParticleEffectNames").getNameByIndex(message.getParticleIndex())
        );
    }

}
