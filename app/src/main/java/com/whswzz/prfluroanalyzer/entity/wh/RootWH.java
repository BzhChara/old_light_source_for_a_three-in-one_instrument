package com.whswzz.prfluroanalyzer.entity.wh;

import java.util.List;

public class RootWH {

	  private String MachineId;	//仪器编号

	    private String SampleNo;	//采样单编号

	    private List<DataWH> RecordList;	//检测数据

		public RootWH(String machineId, String sampleNo, List<DataWH> recordList) {
			super();
			MachineId = machineId;
			SampleNo = sampleNo;
			RecordList = recordList;
		}

		public RootWH() {
			super();
			// TODO Auto-generated constructor stub
		}

		public String getMachineId() {
			return MachineId;
		}

		public void setMachineId(String machineId) {
			MachineId = machineId;
		}

		public String getSampleNo() {
			return SampleNo;
		}

		public void setSampleNo(String sampleNo) {
			SampleNo = sampleNo;
		}

		public List<DataWH> getRecordList() {
			return RecordList;
		}

		public void setRecordList(List<DataWH> recordList) {
			RecordList = recordList;
		}

		@Override
		public String toString() {
			return "RootWH [MachineId=" + MachineId + ", SampleNo=" + SampleNo + ", RecordList=" + RecordList + "]";
		}



	    
	    
}
