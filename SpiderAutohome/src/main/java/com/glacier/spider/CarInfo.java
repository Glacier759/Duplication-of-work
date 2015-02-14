package com.glacier.spider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA on 2015-02-14 19:53.
 * Author:  Glacier (RenLixiang), OurHom.759@gmail.com
 * Company: Class 1204 of Computer Science and Technology
 */
public class CarInfo {
    String car_type, type_link, source;
    String name1, name2, name3;
    String score, peopleNum, rank, price, dealerPriceBox, total;
    List<Petrol> petrolList = new ArrayList<Petrol>();
    List<Type> typeList = new ArrayList<Type>();
    List<Review> reviewList = new ArrayList<Review>();
}
