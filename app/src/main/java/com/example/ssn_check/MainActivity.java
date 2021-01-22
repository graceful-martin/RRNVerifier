/*
    20160658 강상우
 **/

package com.example.ssn_check;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    Toast mToast = null;
    DatePickerDialog dp;
    EditText ssnFirst, ssnSecond;
    TextView resultView;
    private long mLastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setTitle("SSN CHECK");
        toastShort("CHECK SSN 버튼을 눌러\n주민등록번호가 유효한지 확인하세요.");

        ssnFirst = findViewById(R.id.ssn_first);
        ssnSecond = findViewById(R.id.ssn_second);
        final Switch genSwitch = findViewById(R.id.gender_switch);
        resultView = findViewById(R.id.ssn_result);
        Button checkButton = findViewById(R.id.button);

        genSwitch.setOnClickListener(new View.OnClickListener() { // 성별 스위치 텍스트 변경
            @Override
            public void onClick(View v) {
                if(genSwitch.getText().equals("남자")) // 텍스트가 남자일때
                    genSwitch.setText("여자");
                else
                    genSwitch.setText("남자");
            }
        });

        ssnSecond.setOnTouchListener(new View.OnTouchListener() { // ssn_second 클릭
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) { // 중복 클릭 방지
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return false;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    ssnSecond.setText(null); // 초기화
                }
                return false;
            }
        });

        ssnFirst.setOnTouchListener(new View.OnTouchListener() { // ssn_first 클릭
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) { // 중복 클릭 방지
                    return false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                final int d = c.get(Calendar.DAY_OF_MONTH);
                final int m = c.get(Calendar.MONTH);
                final int y = c.get(Calendar.YEAR);
                dp = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String yearString = Integer.toString(year);
                        String parseYear = yearString.substring(2,4);
                        int yearNum = Integer.parseInt(parseYear);
                        ssnFirst.setText(null); // 초기화

                        if((month+1) < 10) // 월이 10보다 작을 시 앞에 0붙이고 아닐 시 안붙임
                            ssnFirst.append(yearNum + "0" + (month+1));
                        else
                            ssnFirst.append("" + yearNum + (month+1));

                        if(dayOfMonth < 10) // 일이 10보다 작을 시 앞에 0붙이고 아닐 시 안붙임
                            ssnFirst.append("0" +dayOfMonth);
                        else
                            ssnFirst.append("" + dayOfMonth);
                    }
                } , y, m, d);
                dp.show();
                return false;
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) { // 중복 클릭 방지
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                String tempString = ssnFirst.getText().toString() + ssnSecond.getText().toString();
                char lastNumber;
                int total = 0;
                if(!checkNum(tempString)) {
                    resultView.setText("주민등록번호가 올바르지 않습니다.");
                    toastShort("주민등록번호가 올바르지 않습니다.");
                } else {
                    if(tempString.length() != 13) {
                        resultView.setText("주민등록번호의 자리 수를 확인해주세요.");
                        toastShort("주민등록번호의 자리 수를 확인해주세요.");
                    } else {
                        lastNumber = tempString.charAt(tempString.length() - 1);
                        String idString = tempString.substring(0, 12);
                        int mul[] = {2, 3, 4, 5, 6, 7, 8, 9, 2, 3, 4, 5};
                        int[] validNum = new int[idString.length()];
                        for (int count = 0; count < idString.length(); count++) {
                            validNum[count] = mul[count] * Character.getNumericValue(idString.charAt(count));
                        }
                        for (int add : validNum) {
                            total += add;
                        }
                        total = 11 - (total % 11);
                        if(total == 10)
                            total = 0;
                        else if(total == 11)
                            total = 1;
                        lastNumber -= '0'; // Char형을 Int로 변환(아스키 코드 방지)
                        if (lastNumber == total) {
                            resultView.setText("정상적인 주민등록번호 입니다.\r\n20160658 강상우");
                            toastShort("정상적인 주민등록번호 입니다.\r\n20160658 강상우");
                        } else {
                            resultView.setText("존재하지 않는 주민등록번호 입니다.");
                            toastShort("존재하지 않는 주민등록번호 입니다.");
                        }

                    }
                  /* 토스트로 배열 전체 출력용(주석)
                  String outputString = "";
                  for(int i : validNum) {
                      outputString += Integer.toString(i) + ",";
                  }
                  toastShort(outputString);
                  */
                }
            }
        });
    }

    public void toastShort(String mToastStr) { // 토스트 출력 메소드
        if(mToast == null) {
            mToast = Toast.makeText(MainActivity.this, mToastStr, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(mToastStr);
        }
        mToast.show();
    }

    public boolean checkNum(String strValue) { // 문자인지 숫자인지 확인 메소드
        try {
            Double.parseDouble(strValue);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}