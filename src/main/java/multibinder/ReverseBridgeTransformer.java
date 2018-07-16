package multibinder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.GenericMessage;

import java.util.concurrent.atomic.AtomicBoolean;

@EnableBinding(ReverseBridgeTransformer.ProcessorR.class)
public class ReverseBridgeTransformer {
	@StreamListener(ProcessorR.INPUT)
	@SendTo(ProcessorR.OUTPUT)
	public Object transformR(Object payload) {
		return payload;
	}
	
	@EnableBinding(TestSourceR.class)
	static class TestProducerR {
		private AtomicBoolean semaphore = new AtomicBoolean(true);

		@Bean
		@InboundChannelAdapter(channel = TestSourceR.OUTPUT, poller = @Poller(fixedDelay = "10000"))
		public MessageSource<String> sendTestDataR() {
			return () ->
					new GenericMessage<>(this.semaphore.getAndSet(!this.semaphore.get()) ? "simply" : "stunning");

		}
	}
		
	@EnableBinding(TestSinkR.class)
	static class TestConsumerR {
		private final Log logger = LogFactory.getLog(getClass());

		@StreamListener(TestSinkR.INPUT)
		public void receiveR(String data) {
			logger.info("Data received..." + data);
		}
	}
	
	interface ProcessorR {
		String INPUT = "inputR";
		String OUTPUT = "outputR";
		
		@Input(INPUT)
		SubscribableChannel inputR();
		
		@Output(OUTPUT)
		MessageChannel outputR();
	}
	
	interface TestSourceR {
		String OUTPUT = "output1R";

		@Output(OUTPUT)
		MessageChannel output1R();

	}

	interface TestSinkR {
		String INPUT = "input1R";

		@Input(INPUT)
		SubscribableChannel input1R();
	}
}
