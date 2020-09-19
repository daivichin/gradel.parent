package com.gradel.parent.component.nodeflow;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//import static org.junit.Assert.fail;

/**
 * @author sdeven.chen.dongwei@gmail.com
 * @date 2017年7月28日 下午2:42:55
 */
public class NodeflowTest {
	private static final Logger logger = LoggerFactory.getLogger(NodeflowTest.class);

	//@Test
	public void testExecute() {
		try {
			String nfJson = IOUtils.toString(NodeflowTest.class.getResourceAsStream("/test.nf"));
			new Nodeflow(nfJson).execute();
		} catch (IOException e) {
			e.printStackTrace();
			//fail("error");
		} catch (NodeflowException e) {
			e.printStackTrace();
			//fail("error");
		}
	}
	//@Test
	public void testHlhCredit() {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("sexual", 0);
			params.put("age", "40");
			String nfJson = IOUtils.toString(NodeflowTest.class.getResourceAsStream("/hlh_credit.nf"));
			new Nodeflow(nfJson).execute(params);
			logger.debug("result:{} {}", params.get("sexualScore"), params.get("ageScore"));
		} catch (IOException e) {
			e.printStackTrace();
			//fail("error");
		} catch (NodeflowException e) {
			e.printStackTrace();
			//fail("error");
		}
	}

}
