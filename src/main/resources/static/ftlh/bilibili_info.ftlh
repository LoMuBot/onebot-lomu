<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            font-weight: 500;
            font-family: Arial, Helvetica, sans-serif;
        }

        .box {
            width: 500px;
            height: 500px;
            overflow: hidden;
        }

        .subbox {
            border: 2px solid #ffffff;
            border-radius: 0 30px 30px 30px;
            width: 400px;
            height: 400px;
            background-color: aliceblue;
            margin: 0 auto;
            overflow: hidden;
        }

        .info {
            width: 100%;
            height: 240px;
            background-image: url("${video_bg}");
            background-size: 100% 100%;
            border-radius: 8px;
        }

        h3 {
            display: inline-block;
            height: 30px;
            width: 200px;
            line-height: 30px;
            text-align: center;
            font-size: 18px;
            border-radius: 8px 8px 0 0;
            font-weight: 600;
            margin-left: 48px;
            margin-top: 20px;
            border-top: 2px solid #ffffff;
            border-left: 2px solid #ffffff;
            border-right: 2px solid #ffffff;
            color: #ffffff;
            background-color: #fbc2eb;
        }

        .title {
            padding: 15px 20px 15px 20px;
            font-size: 20px;
            font-weight: 700;
        }

        .avatar {
            width: 45px;
            height: 45px;
            margin-left: 20px;
            background-image: url("${avatar}");
            background-size: cover;
            border: 1px solid #ffffff;
            border-radius: 15px;
            float: left
        }

        .up_info {
            width: 100%;
            height: 50px;
            line-height: 50px;
        }

        h4 {
            float: left;
            color: #f784d6;
            font-size: 20px;
            padding-left: 10px;
            max-width: 300px;
        }

        .detail {
            padding-left: 10px;
            font-size: 8px;
            color: rgba(0, 0, 0, 0.541);
        }

        body {
            background-image: url("/images/bilibili_bg.png");
            background-size: cover;
            background-repeat: no-repeat;
        }
    </style>
</head>

<body>
<div class="box">
    <h3>LoMu-Bot</h3>
    <div class="subbox">
        <div class="up_info">
            <span class="avatar"></span>
            <h4>${up_name}</h4>
        </div>
        <div class="info">

        </div>
        <p class="title"> ${title}</p>
        <hr>
        <p class="detail">${desc}</p>
    </div>
</div>
</body>

</html>