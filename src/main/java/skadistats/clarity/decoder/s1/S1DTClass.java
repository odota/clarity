package skadistats.clarity.decoder.s1;

import skadistats.clarity.model.DTClass;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.model.ImmutableFieldPath;
import skadistats.clarity.model.state.Accessor;

import java.util.HashMap;
import java.util.Map;

public class S1DTClass implements DTClass {

    private final String dtName;
    private final SendTable sendTable;
    private int classId = -1;
    private ReceiveProp[] receiveProps;
    private int[] indexMapping;
    private Map<String, Integer> propsByName;
    private S1DTClass superClass;
    private int memoryRequirement;

    public S1DTClass(String dtName, SendTable sendTable) {
        this.dtName = dtName;
        this.sendTable = sendTable;
    }

    @Override
    public int getClassId() {
        return classId;
    }

    @Override
    public void setClassId(int classId) {
        this.classId = classId;
    }

    @Override
    public Accessor getSubAccessor(int i) {
        return receiveProps[i].getAccessor();
    }

    @Override
    public Integer getSubStateLength() {
        return receiveProps.length;
    }

    @Override
    public int getNeededMemorySize() {
        return memoryRequirement;
    }

    @Override
    public String getNameForFieldPath(FieldPath fp) {
        return this.receiveProps[fp.getElement(0)].getVarName();
    }

    @Override
    public FieldPath getFieldPathForName(String name){
        Integer idx = this.propsByName.get(name);
        return idx != null ? new ImmutableFieldPath(idx.intValue()) : null;
    }

    public S1DTClass getSuperClass() {
        return superClass;
    }

    public void setSuperClass(S1DTClass superClass) {
        this.superClass = superClass;
    }

    public String getDtName() {
        return dtName;
    }
    
    public boolean instanceOf(String dtName) {
        S1DTClass s = this;
        while (s != null) {
            if (s.getDtName().equals(dtName)) {
                return true;
            }
            s = s.getSuperClass();
        }
        return false;
    }
    
    public SendTable getSendTable() {
        return sendTable;
    }

    public ReceiveProp[] getReceiveProps() {
        return receiveProps;
    }

    public void setReceiveProps(ReceiveProp[] receiveProps) {
        this.receiveProps = receiveProps;
        this.propsByName = new HashMap<>();
        for(int i = 0; i < receiveProps.length; ++i) {
            this.propsByName.put(receiveProps[i].getVarName(), i);
            memoryRequirement += receiveProps[i].getAccessor().getNeededMemorySize();
        }
    }

    public int[] getIndexMapping() {
        return indexMapping;
    }

    public void setIndexMapping(int[] indexMapping) {
        this.indexMapping = indexMapping;
    }

}
