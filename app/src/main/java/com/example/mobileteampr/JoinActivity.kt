package com.example.mobileteampr

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobileteampr.databinding.ActivityJoinBinding
import com.google.firebase.database.*

class JoinActivity : AppCompatActivity() {
    lateinit var binding : ActivityJoinBinding
    lateinit var rdb:DatabaseReference
    var isPw = true // 수정요망
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        rdb = FirebaseDatabase.getInstance().getReference("All/users") // All DB안에 users 테이블
        binding.apply{
            var isSame = false
            checkIdBtn.setOnClickListener {
                // 닉네임 중복확인
                val id = joinId.text.toString()
                rdb.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (user in snapshot.children) {
                            if (user.key.toString() == id) { // 이미 존재
                                isSame = true
                            }
                        }
                        if(isSame){
                            checkIdView.setText(R.string.id_exist)
                            // isSame = false
                            Log.d("isSame", "$isSame")
                        }
                        else {
                            checkIdView.setText(R.string.id_done)
                            Log.d("isSame", "$isSame")
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@JoinActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
                    }
                })
            }


            approvedBtn.setOnClickListener {
                val id = joinId.text.toString()
                val pw = joinPw.text.toString()
                // 중복확인 및 비밀번호 조건 만족시 : 수정 요망
                if((checkIdView.text.equals(R.string.id_done)) && isPw) {
                    insertNewUser(id, pw)
                    Toast.makeText(this@JoinActivity, "가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    // 가입완료 후 뒤로 돌아가서 로그인 할지 & 로그인 액티비티로 넘어가게 할지 수정 요망
                }
                else{
                    Toast.makeText(this@JoinActivity, "닉네임 또는 비밀번호를 확인하세요.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun checkId(id:String):Boolean{
        var flag = true // default : true (가능)
        Log.d("1", "input = $id")
        rdb = FirebaseDatabase.getInstance().getReference("All/users")
        rdb.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (user in snapshot.children) {
                    if (user.key.toString() == id) { // 이미 존재
                        flag = false
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@JoinActivity, "네트워크 오류", Toast.LENGTH_SHORT).show()
            }
        })
        if(!flag)
            Log.d("2", " false")
        else
            Log.d("2", "true")
        return flag
    }

    private fun checkPw(pw:String):Boolean{ // 비밀번호 조건 : 영소문자+숫자
        val pwReg = Regex("^(?=.*[a-z])(?=.*[0-9]).\$")
        return pw.matches(pwReg)
    }

    private fun insertNewUser(id:String, pw:String){
        val user = User(id, pw)
        rdb.child(id).setValue(user) // key 값 : id
    }

}