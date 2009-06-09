package splitsuc;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import org.apache.pig.EvalFunc;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.data.DataType;

public class SPLITSUC extends EvalFunc<DataBag> {
    TupleFactory mTupleFactory = TupleFactory.getInstance();
    BagFactory mBagFactory = BagFactory.getInstance();
		Integer numberOfKeys = 3;

    public DataBag exec(Tuple input) throws IOException {

      if (input == null)
      	return null;
      try {
	      DataBag output = mBagFactory.newDefaultBag();
	      Object o = input.get(0);
	      if (!(o instanceof String)) {
	          throw new IOException("Expected input to be chararray, but  got " + o.getClass().getName());
	      }
      
				String[] words = ((String)o).split("\\W");
				PriorityQueue<String> keys = new PriorityQueue<String>();
				for (String word : words) {
				  if (!word.equals("")) {			
						if (keys.size() >= numberOfKeys) {
							List<Tuple> currentList = new ArrayList<Tuple>();
							currentList.add(mTupleFactory.newTuple(keys.toArray()));
							currentList.add(mTupleFactory.newTuple(word));
							output.add(mTupleFactory.newTuple(currentList));
							keys.remove();
						}
						keys.add(word);
					}
				}
	       return output;
			} catch (Exception e) {
				System.err.println("Failed to process input in SplitSuc; error - " + e.getMessage());
        return null;
 			}
		}

    public Schema outputSchema(Schema input) {
      try{
	
				Schema keyTupleSchema = new Schema();
								
				int factorial = 1;
				for (int count=1; count <= numberOfKeys; count++) {
					keyTupleSchema.add(new Schema.FieldSchema("word" + count.toString(), DataType.CHARARRAY));
				}
				
				Schema.FieldSchema keyTupleFs;
        keyTupleFs = new Schema.FieldSchema("keyTuple", keyTupleSchema, DataType.TUPLE);
				
				Schema.FieldSchema successor = new Schema.FieldSchema("successor", DataType.CHARARRAY);

        Schema tupleSchema = new Schema();
				tupleSchema.add(keyTupleFs);
				tupleSchema.add(successor);

        Schema.FieldSchema tupleFs;
        tupleFs = new Schema.FieldSchema("keySuccessorTuple", tupleSchema, DataType.TUPLE);

        Schema bagSchema = new Schema(tupleFs);
        bagSchema.setTwoLevelAccessRequired(true);
        Schema.FieldSchema bagFs = new Schema.FieldSchema("BagOfSuccessorTuple",bagSchema, DataType.BAG);
        
        return new Schema(bagFs); 

      }catch (Exception e){
              return null;
      }
		}
}

