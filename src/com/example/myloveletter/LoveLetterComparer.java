package com.example.myloveletter;

import java.util.Comparator;

import com.example.myloveletter.loveletterendpoint.model.LoveLetter;

public class LoveLetterComparer implements Comparator<LoveLetter> {

	@Override
	public int compare(LoveLetter A, LoveLetter B) {
		// TODO Auto-generated method stub
		long a = A.getLoveLetterDate().getValue();
		long b = B.getLoveLetterDate().getValue();
		return a < b ? -1
		     : a > b ? 1
		     : 0;
	}

}
