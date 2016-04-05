#!/bin/bash

RESULT_FILE="Ex1.txt"

touch $RESULT_FILE

COUNTER_GOAL="300000"
NUM_RUNS="50"


java Ex1 4 $COUNTER_GOAL false false $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

java Ex1 8 $COUNTER_GOAL false false $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

java Ex1 4 $COUNTER_GOAL true false $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

java Ex1 8 $COUNTER_GOAL true false $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

COUNTER_GOAL="30000" >> ${RESULT_FILE}
echo "" >> ${RESULT_FILE}
echo "Changed counter-goal for 1 processor case (takes too long otherwise)" >> ${RESULT_FILE}
echo "" >> ${RESULT_FILE}

java Ex1 4 $COUNTER_GOAL false true $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

java Ex1 8 $COUNTER_GOAL false true $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

java Ex1 4 $COUNTER_GOAL true true $NUM_RUNS >> ${RESULT_FILE}
echo "================================== " >> ${RESULT_FILE}

java Ex1 8 $COUNTER_GOAL true true $NUM_RUNS >> ${RESULT_FILE}
