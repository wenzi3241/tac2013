
package edu.knowitall.tac2013.findSlotFillersApp

import QueryEntityForAllSlots.executeEntityQueryForAllSlots
import QueryEntityForAllSlots.executeEntityQueryForAllSlotsWithoutFilter
import KbpQueryOutput.printUnformattedOutput
import KbpQueryOutput.printFormattedOutput
import SlotFillReranker.chooseBestTest


//Command line application object for running solr queries on all the slots
//of a given entity and semantic type
object FindSlotFills {
  
  
  def main(args: Array[String]){

     assert(args.length == 3, 
         "there should be three arguments: entity name, semantic type (organization or person), and file path for output")
         
     val entityName = args(0).replace("_", " ")
     val semanticType = args(1)
     println(entityName)
     println(semanticType)
     
     if (semanticType == "organization"){
       
	     val mapOfResults = executeEntityQueryForAllSlots(entityName, SlotPattern.organizationPatterns)
	     
	     // rank candidate extractions and build a map from slot names to SlotCandidateSet
	     var slotCandidateSetMap = Map[String,SlotCandidateSet]()
	     for( x <- mapOfResults.keys){
	        slotCandidateSetMap += ( x -> new SlotCandidateSet(entityName,mapOfResults(x)));
	        slotCandidateSetMap(x).setRankedAnswers(chooseBestTest(slotCandidateSetMap(x).candidateSets));
	     }
	     
	     
	     printUnformattedOutput(slotCandidateSetMap,args(2),KBPQueryEntityType.ORG)
	     printFormattedOutput(slotCandidateSetMap,args(2),KBPQueryEntityType.ORG)

  	 }
     
     else if (semanticType == "person"){
       
	     val mapOfResults = executeEntityQueryForAllSlots(entityName, SlotPattern.personPatterns)
	     
	     // rank candidate extractions and build a map from slot names to SlotCandidateSet
	     var slotCandidateSetMap = Map[String,SlotCandidateSet]()
	     for( x <- mapOfResults.keys){
	        slotCandidateSetMap += ( x -> new SlotCandidateSet(entityName,mapOfResults(x)));
	        slotCandidateSetMap(x).setRankedAnswers(chooseBestTest(slotCandidateSetMap(x).candidateSets));
	     }
	     
	     
	     printUnformattedOutput(slotCandidateSetMap,args(2),KBPQueryEntityType.PER)
	     printFormattedOutput(slotCandidateSetMap,args(2),KBPQueryEntityType.PER)
     }
     
     else{
       
       throw new IllegalArgumentException("Second Argument must be either 'person' or 'organization'")
       
     }
  }
  
    def runForServerOutput(field1: String, field2:String, nodeId: Option[String] = None): String = {


         
     val entityName = field1.replace("_", " ").trim()
     val semanticType = field2.trim()

     
     if (semanticType == "organization"){
	     val orgMap = SlotPattern.organizationPatterns
	     val mapOfResults = executeEntityQueryForAllSlots(entityName, orgMap.toMap,nodeId)
	     val unfilteredMapOfResults = executeEntityQueryForAllSlotsWithoutFilter(entityName, orgMap.toMap,nodeId)
	     
	     //build a map from slot names to SlotCandidateSet
	     var filteredSlotCandidateSetMap = Map[String,SlotCandidateSet]()
	     for( x <- mapOfResults.keys){
	        filteredSlotCandidateSetMap += ( x -> new SlotCandidateSet(entityName,mapOfResults(x)))
	        filteredSlotCandidateSetMap(x).setRankedAnswers(chooseBestTest(filteredSlotCandidateSetMap(x).candidateSets));
	     }
	     var unFilteredSlotCandidateSetMap = Map[String,SlotCandidateSet]()
	     for( x <- unfilteredMapOfResults.keys){
	        unFilteredSlotCandidateSetMap += ( x -> new SlotCandidateSet(entityName,unfilteredMapOfResults(x)))
	     }
	     
	     return (
	     "\n-----------------------------------------\nUNFILTERED RESULTS\n--------------------------------------\n\n" +
	     printUnformattedOutput(unFilteredSlotCandidateSetMap,KBPQueryEntityType.ORG) + 
	     "\n-----------------------------------------\nFILTERED RESULTS\n--------------------------------------\n\n" +
	     printUnformattedOutput(filteredSlotCandidateSetMap,KBPQueryEntityType.ORG) + 
	     "\n-----------------------------------------\nFORMATTED RESULTS\n--------------------------------------\n\n" +
	     printFormattedOutput(filteredSlotCandidateSetMap,KBPQueryEntityType.ORG) 
	     )

  	 }
     
     else if (semanticType == "person"){
	     val perMap = SlotPattern.personPatterns
	     val mapOfResults = executeEntityQueryForAllSlots(entityName, perMap.toMap,nodeId)
	     val unfilteredMapOfResults = executeEntityQueryForAllSlotsWithoutFilter(entityName, perMap.toMap,nodeId)
	     
	     
	     //build a map from slot names to SlotCandidateSet
	     var filteredSlotCandidateSetMap = Map[String,SlotCandidateSet]()
	     for( x <- mapOfResults.keys){
	        filteredSlotCandidateSetMap += ( x -> new SlotCandidateSet(entityName,mapOfResults(x)));
	        filteredSlotCandidateSetMap(x).setRankedAnswers(chooseBestTest(filteredSlotCandidateSetMap(x).candidateSets));
	     }
	     var unFilteredSlotCandidateSetMap = Map[String,SlotCandidateSet]()
	     for( x <- unfilteredMapOfResults.keys){
	        unFilteredSlotCandidateSetMap += ( x -> new SlotCandidateSet(entityName,unfilteredMapOfResults(x)))
	     }
	     
	     
	     return (
	     "\n-----------------------------------------\nUNFILTERED RESULTS\n--------------------------------------\n\n" +
	     printUnformattedOutput(unFilteredSlotCandidateSetMap,KBPQueryEntityType.PER) + 
	     "\n-----------------------------------------\nFILTERED RESULTS\n--------------------------------------\n\n" +
	     printUnformattedOutput(filteredSlotCandidateSetMap,KBPQueryEntityType.PER) + 
	     "\n-----------------------------------------\nFORMATTED RESULTS\n--------------------------------------\n\n" +
	     printFormattedOutput(filteredSlotCandidateSetMap,KBPQueryEntityType.PER) 
	     )
     }
     
     else{
       
       throw new IllegalArgumentException("Second Argument must be either 'person' or 'organization'")
       
     }
  }

}