package ch.yax.hive.udf.text;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF.DeferredObject;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.FloatObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.IntObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.junit.Assert;
import org.junit.Test;

public class TestSuggestion {

	@Test
	public void testInitNotSize3() throws UDFArgumentException {
		Suggestion suggestion = new Suggestion();
		ObjectInspector[] args = new ObjectInspector[1];
		try {
			suggestion.initialize(args);
			Assert.fail();
		} catch (UDFArgumentException ex) {

		}
	}

	@Test
	public void testInitEmpty() throws UDFArgumentException {
		Suggestion suggestion = new Suggestion();
		ObjectInspector[] args = new ObjectInspector[5];
		suggestion.initialize(args);
	}

	@Test
	public void testInitNull() throws UDFArgumentException {
		Suggestion suggestion = new Suggestion();
		try {
			suggestion.initialize(null);
			Assert.fail();
		} catch (UDFArgumentException ex) {

		}
	}

	@Test
	public void testEvaluateNull() throws HiveException {
		Suggestion suggestion = new Suggestion();
		ObjectInspector[] args = new ObjectInspector[5];
		initObjInspectors(args);

		try {
			suggestion.initialize(null);
			Assert.fail();
		} catch (HiveException ex) {

		}
	}

	@Test
	public void testEvaluateEmpty() throws HiveException {
		Suggestion suggestion = new Suggestion();
		ObjectInspector[] args = new ObjectInspector[5];
		initObjInspectors(args);

		suggestion.initialize(args);
		DeferredObject[] dobjs = new DeferredObject[5];
		try {
			suggestion.evaluate(dobjs);
			Assert.fail();
		} catch (HiveException ex) {

		}
	}

	@Test
	public void testEvaluateWithNullValues() throws HiveException {
		Suggestion suggestion = new Suggestion();
		ObjectInspector[] args = new ObjectInspector[5];
		initObjInspectors(args);

		suggestion.initialize(args);
		DeferredObject[] dobjs = new DeferredObject[5];
		DeferredObject strategy = mock(DeferredObject.class);
		DeferredObject target = mock(DeferredObject.class);
		DeferredObject other = mock(DeferredObject.class);

		dobjs[0] = strategy;
		dobjs[1] = target;
		dobjs[2] = other;

		try {
			suggestion.evaluate(dobjs);
			Assert.fail();
		} catch (HiveException ex) {

		}

	}

	@Test
	public void testEvaluateWith_LEVENSTEIN() throws HiveException {
		Assert.assertEquals("BERN",
				testStrategy(Strategy.LEVENSTEIN, "i love the city of Bern"));

		Assert.assertEquals("BERN",
				testStrategy(Strategy.LEVENSTEIN, "i love the city of bärn"));

		Assert.assertEquals("ZÜRICH",
				testStrategy(Strategy.LEVENSTEIN, "i love the city of zurich"));

	}

	@Test
	public void testEvaluateWith_JAROWINKLER() throws HiveException {
		Assert.assertEquals("BERN",
				testStrategy(Strategy.JAROWINKLER, "i love the city of berns"));

		Assert.assertEquals("BERN",
				testStrategy(Strategy.JAROWINKLER, "i love the city of bern"));

		Assert.assertEquals("ZÜRICH",
				testStrategy(Strategy.JAROWINKLER, "i love the city of zurich"));

		Assert.assertEquals("GENÈVE",
				testStrategy(Strategy.JAROWINKLER, "i like the city of genev"));
	}

	@Test
	public void testEvaluateWith_BIGRAM() throws HiveException {
		Assert.assertEquals("BERN",
				testStrategy(Strategy.BIGRAM, "i love the city of bern"));
	}

	private String testStrategy(Strategy distanceStrategy, String text)
			throws UDFArgumentException, HiveException {
		Suggestion suggestion = new Suggestion();
		ObjectInspector[] args = new ObjectInspector[5];
		initObjInspectors(args);

		suggestion.initialize(args);
		DeferredObject[] dobjs = new DeferredObject[5];
		DeferredObject strategy = mock(DeferredObject.class);
		DeferredObject target = mock(DeferredObject.class);
		DeferredObject file = mock(DeferredObject.class);
		DeferredObject minThreashold = mock(DeferredObject.class);
		DeferredObject minTokenLength = mock(DeferredObject.class);

		dobjs[0] = strategy;
		dobjs[1] = target;
		dobjs[2] = file;
		dobjs[3] = minThreashold;
		dobjs[4] = minTokenLength;

		when(((StringObjectInspector) args[0]).getPrimitiveJavaObject(any()))
				.thenReturn(distanceStrategy.getName().toLowerCase());
		when(((StringObjectInspector) args[1]).getPrimitiveJavaObject(any()))
				.thenReturn(text);
		when(((StringObjectInspector) args[2]).getPrimitiveJavaObject(any()))
				.thenReturn("/ch.places.small.txt");
		when(((FloatObjectInspector) args[3]).getPrimitiveJavaObject(any()))
				.thenReturn(new Float(8.5f));
		when(((IntObjectInspector) args[4]).getPrimitiveJavaObject(any()))
				.thenReturn(new Integer(4));

		Object result = suggestion.evaluate(dobjs);
		Assert.assertNotNull(result);
		return (String) result;

	}

	private void initObjInspectors(ObjectInspector[] args) {
		args[0] = mock(StringObjectInspector.class);
		args[1] = mock(StringObjectInspector.class);
		args[2] = mock(StringObjectInspector.class);
		args[3] = mock(FloatObjectInspector.class);
		args[4] = mock(IntObjectInspector.class);

	}

}
