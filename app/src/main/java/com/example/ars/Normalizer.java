package com.example.ars;

import org.nd4j.linalg.api.buffer.DataType;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import static org.nd4j.linalg.ops.transforms.Transforms.sqrt;

public class Normalizer {
    INDArray n, mean, meanDiff, var;

    public Normalizer(int nbInputs){
        this.n = Nd4j.zeros(DataType.DOUBLE, nbInputs);
        this.mean = Nd4j.zeros(DataType.DOUBLE,nbInputs);
        this.meanDiff = Nd4j.zeros(DataType.DOUBLE,nbInputs);
        this.var = Nd4j.zeros(DataType.DOUBLE,nbInputs);
    }


    public void observe(INDArray x){
        this.n = this.n.add(1);
        INDArray lastMean = this.mean.dup();
        this.mean = this.mean.add((x.sub(this.mean)).div(this.n));

        this.meanDiff = this.meanDiff.add((x.sub(lastMean)).mul(x.sub(this.mean)));
        this.var = (this.meanDiff.div(this.n)).add(1E-3);
    }


    public INDArray normalize(INDArray x){

        INDArray obsMean = this.mean;
        INDArray obsStd = sqrt(this.var);
        return (x.sub(obsMean)).div(obsStd);
    }
}
