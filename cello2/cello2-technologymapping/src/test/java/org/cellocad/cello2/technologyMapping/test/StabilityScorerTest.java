package org.cellocad.cello2.technologyMapping.test;

import java.io.IOException;

import org.cellocad.cello2.common.Utils;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.sequential.StabilityScorer;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.Gate;
import org.cellocad.cello2.technologyMapping.algorithm.SimulatedAnnealing.data.ucf.ResponseFunction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class StabilityScorerTest {

	private JSONObject json(String file) throws IOException, ParseException {
		JSONObject rtn = null;
		String str = Utils.getResourceAsString(file);
		JSONParser parser = new JSONParser();
		rtn = (JSONObject) parser.parse(str);
		return rtn;
	}

	private ResponseFunction responseFunction(String name) throws IOException, ParseException {
		ResponseFunction rtn = null;
		JSONObject obj = json(name + ".rf.json");
		rtn = new ResponseFunction(obj);
		return rtn;
	}

	private Gate gate(String name) throws IOException, ParseException {
		Gate rtn = null;
		JSONObject obj = json(name + ".gate.json");
		rtn = new Gate(obj);
		ResponseFunction rf = responseFunction(name);
		rtn.setResponseFunction(rf);
		return rtn;
	}

	@Test
	public void test() throws IOException, ParseException {
		Gate gate1 = null;
		Gate gate2 = null;
		StabilityScorer scorer = null;
		Double score = null;

		gate1 = gate("S3_SrpR");
		gate2 = gate("P1_PhlF");
		scorer = new StabilityScorer(gate1, gate2);
		score = scorer.score();

		assert (score >= 0.44 && score <= 0.56);

		gate2 = gate("P2_PhlF");
		scorer = new StabilityScorer(gate1, gate2);
		score = scorer.score();
		assert (score >= 0.20 && score <= 0.26);
	}

}
