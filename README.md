# GfPermission

  封装的权限请求框架，自己练手使用这一个中有两套框架使用方法不同，可按需使用。
  
  How to
  
  To get a Git project into your build:

  Step 1. Add the JitPack repository to your build file

  gradle
  maven
  sbt
  leiningen
  Add it in your root build.gradle at the end of repositories:

    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }
  Step 2. Add the dependency

    dependencies {
            implementation 'com.github.gaof5:GfPermission:v1.0'
    }
  
  ### 方法一：运用注解、实现PermissionCallback方式配合使用，用于某activity进行请求，步骤2、3可写在基类中方便使用
  
  1.在需要请求权限的操作中添加此方法
```
  //注解用于申请权限后再重复执行此方法，CAMERA_CODE为请求标识码
  @IPermission(CAMERA_CODE)
  private void cameraTask() {
      //先判断是否已申请此权限
      if(PermissionManager.hasPermission(this, Manifest.permission.CAMERA)){
          //如果已申请权限则执行操作
          Toast.makeText(this,"相机权限拿到拍照",Toast.LENGTH_SHORT).show();
      }else {//没有权限申请
          PermissionManager.requestPermissions(this,"需要相机权限拍照",CAMERA_CODE,Manifest.permission.CAMERA);
      }
  }

```
  2.在activity中重新onRequestPermissionsResult方法，调用 PermissionManager.onRequestPermissionResult 方法
  ```
  @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionResult(requestCode,permissions,grantResults,this);
    }
  ```
  3.进行完1、2步骤完后直接通过权限申请会直接执行cameraTask()方法即执行此段代码：
  ```
  Toast.makeText(this,"相机权限拿到拍照",Toast.LENGTH_SHORT).show();
  ```
  如用户拒绝权限，并且勾选了不再询问，则需要Activity实现PermissionCallback
  ```
  @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        //已同意权限
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        //检查用户是否拒绝过权限，并且点击了 不再询问
        if(PermissionManager.somePermissionPermanentlyDenied(this,perms)){
            //显示一个对话框告诉开启 此操作可自行处理
            new AppSettingDialog.Builder(this).setTitle("权限申请")
                    .setRationale("需要请求")
                    .setNegativeButton("取消")
                    .setPositiveButton("设置")
                    .setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).build().show();
        }
    }
  ```
  ### 方法二：方法二接入流程有些肯觉得复杂，虽然写入基类后使用还好。因此重做了一套框架使用直接调用，最终操作在回调中进行
  此方法无需先判断是否已申请了权限，权限请求成功都会回调setPermissionCallback onPermissionGranted方法
  ```
      String[] perms=new String[]{Permission.ACCESS_FINE_LOCATION,Permission.READ_CONTACTS};
      GfPermission.with(this)
              .setPermissions("需要定位、联系人权限发送位置",LOCATION_CONTACTS_CODE,perms)
                //如已设置.rationale（）拒绝权限并勾选了不再询问，则会执行rationale中回调，不设置.rationale（）会默认弹框引导设置
//                .rationale(new RationaleCallback() {
//                    @Override
//                    public void onPermissionDenied(int requestCode, List<String> perms) {
//                        Toast.makeText(MainActivity.this,"不再询问处理",Toast.LENGTH_SHORT).show();
//                    }
//                })
              .setPermissionCallback(new PermissionCallback() {
                  @Override
                  public void onPermissionGranted(int requestCode, List<String> perms) {
                      //拿到权限进行自己的操作
                      Toast.makeText(MainActivity.this,"定位、联系人权限拿到发送位置",Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onPermissionDenied(int requestCode, List<String> perms) {
                      //用户拒绝权限后的回调 
                      //1.如用户勾选了不再询问,如未设置.rationale（）则会默认弹框引导设置，不用在此回调操作
                      //如已设置.rationale（）则会执行rationale中回调 
                      //2.用户未勾选了不再询问,框架只会回调此方法，不会进行其他操作
                      Toast.makeText(MainActivity.this,"拒绝权限",Toast.LENGTH_SHORT).show();
                  }
              }).request();//request()发起请求在最后调用
  ```
      
      上注释比较多为了解释更清楚，可能看起来比较多下面去掉注释给大家看下调用
```
      String[] perms=new String[]{Permission.ACCESS_FINE_LOCATION,Permission.READ_CONTACTS};
      GfPermission.with(this)
              .setPermissions("需要定位、联系人权限发送位置",LOCATION_CONTACTS_CODE,perms)
              .setPermissionCallback(new PermissionCallback() {
                  @Override
                  public void onPermissionGranted(int requestCode, List<String> perms) {
                      //拿到权限进行自己的操作
                      Toast.makeText(MainActivity.this,"定位、联系人权限拿到发送位置",Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onPermissionDenied(int requestCode, List<String> perms) {
                      
                  }
              }).request();
```
  
