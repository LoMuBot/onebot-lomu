<style>
    * {
        padding: 0;
        margin: 0;
        font-family: '微软雅黑';
    }


    #content-container {
        height: 1080px;
        width: 1200px;
        background-image: url(https://cdn.playeternalreturn.com/event/season7/global_bt/bg_deco02.png);
        background-size: cover;
        background-position: center;
        padding: 20px;
    }

    #header {
        border-radius: 10px;
        height: 176px;
        width: 100%;
        background-size: cover;
        background-position: center;
        background-image: url(https://cdn.dak.gg/er/images/bg/bg-landing-search-v7.jpg);
        float: left;
        overflow: hidden;
    }


    .profile-image-wrapper,
    #header>.profile-image-wrapper img {
        width: 222px;
        height: 176px;
        float: left;
    }


    #top {
        width: 355px;
        height: 150px;
        float: left;
        margin-left: 20px;
        margin-top: 50px;
        text-align: center;
        color: aliceblue;
    }

    #header .nickname {
        margin: 10px;
        font-size: 28px;

    }

    #logo {
        color: #808080;
        margin-right: 10px;
        margin-top: 150px;
        float: right;
        font-size: 10px;
        height: 20px;
        line-height: 20px;
        width: 85px;
        text-align: center;
        border: 1px #808080 solid;
        border-radius: 8px;
    }

    #header .level {
        margin-left: 75px;
        width: 70px;
        border-radius: 30px;
        border: 2px solid #fff;
    }

    #body {
        float: left;
        width: 100%;
        height: 840px;
        margin-top: 20px;
    }

    #left {
        width: 352px;
        height: 1080px;
        background-color: rgba(255, 255, 255, 0.8);
        float: left;
    }


    #right {
        padding: 20px;
        margin-left: 20px;
        width: 775px;
        height: 100%;
        float: left;
    }

    #rank {
        overflow: hidden;
        width: 100%;
        height: 300px;
        text-align: center;
    }

    #rank>h4 {
        padding: 10px 0;
        font-size: 20px;
        border-bottom: 1px solid #8d8a8a;
    }

    #rank #score {
        padding: 15px 0;
        text-align: center;
        border-bottom: 1px solid #8d8a8a;
    }

    #rp_img,
    #rp_box {
        display: inline-block;
    }

    #rp_img {
        height: 64px;
        width: 64px;
    }

    #rp {
        font-size: 25px;
        font-weight: 900;
        color: chocolate;
    }

    #rp_name {
        font-size: 12px;
        font-weight: 900;
    }


    #rp_rank,
    #rp_local_rank {
        font-size: 10px;
    }

    #record {
        overflow: hidden;
        height: 135px;
        margin-top: 10px;
        border-bottom: 1px solid #8d8a8a;
        text-align: center;
    }


    .record_box {
        border-bottom: 1px solid;
        display: inline-block;
        width: 100px;
        height: 40px;
    }

    .record_box>h4 {
        font-size: 13px;
        font-weight: 700;
    }



    .war_record {
        margin-bottom: 10px;
        height: 200px;
        width: 820px;
        border-radius: 20px;
        position: relative;
        background-color: rgb(255, 255, 255);
    }
    body {
        background-color: #e7e3e3;
    }

    .war_record_small {
        margin-bottom: 10px;
        height: 100px;
        width: 820px;
        border-radius: 20px;
        position: relative;
        background-color: rgb(255, 255, 255);
    }

    .war_record_left_large_top1,
    .war_record_left_large_top2,
    .war_record_left_large_top3 {
        border-radius: 20px 0 0 20px;
        height: 200px;
        width: 15px;
        position: absolute;
    }

    .war_record_right_large_top1,
    .war_record_right_large_top2,
    .war_record_right_large_top3 {
        border-radius: 0 20px 20px 0;
        height: 200px;
        width: 15px;
        position: absolute;
        right: 0;
    }

    .war_record_left_small_top1,
    .war_record_left_small_top2,
    .war_record_left_small_top3 {
        border-radius: 20px 0 0 20px;
        height: 100px;
        width: 15px;
        position: absolute;
    }

    .war_record_right_small_top1,
    .war_record_right_small_top2,
    .war_record_right_small_top3 {
        border-radius: 0 20px 20px 0;
        height: 100px;
        width: 15px;
        right: 0;
        position: absolute;
    }


    .war_record_left_large_top1,
    .war_record_right_large_top1,
    .war_record_left_small_top1,
    .war_record_right_small_top1 {
        background-color: chartreuse;
    }

    .war_record_left_large_top2,
    .war_record_right_small_top2,
    .war_record_right_large_top2,
    .war_record_left_small_top2 {
        background-color: #329ce0;
    }

    .war_record_left_large_top3,
    .war_record_right_large_top3,
    .war_record_right_small_top3,
    .war_record_left_small_top3 {
        background-color: #4b525d;
    }

    .war_rank {
        margin-top: 10px;
        width: 60px;
        height: 66px;
        padding-left: 25px;
        font-size: 14px;
        float: left;
    }

    .war_record_character_info {
        margin-top: 4px;
        float: left;
        width: 54px;
        height: 74px;
        text-align: center;
    }

    .war_record_character_info .character_name {
        font-size: 12px;
    }



    .hero_avatar,
    .hero_avatar>img {
        width: 54px;
        height: 64px;
    }

    .hero_avatar>img {
        border-radius: 50%;
    }

    .war_record1 {
        height: 98px;
        width: 100% - 10;
        overflow: hidden;
    }

    .war_record2 {
        overflow: hidden;
        height: 50px;
        width: 100% - 10;
        border-top: 1px solid #8d8a8a;
    }



    .war_record2 .hero_avatar,
    .war_record2 .hero_avatar>img {
        float: left;
        width: 27.55px;
        height: 32.65px;
        margin-top: 2.55px;
    }




    .war_record2 .play_data {
        margin-top: 15px;
        margin-left: 8px;
        height: 35.2px;
    }

    .war_record2 .play_data span {
        font-size: 7px;
    }

    .war_record2 .skill {
        margin-top: 5px;
        margin-left: 5px;
        width: 48px;
        height: 48px;

    }


    .weapon {
        border-radius: 20px;
        background-color: #323232;
    }

    .war_record2 .play_stat {
        height: 27.55px;
        margin-top: 12.75px;
    }

    .war_record2 .weapon,
    .war_record2 .trait {
        width: 16.24px;
        height: 16.24px;
    }



    .war_record2 .stat,
    .war_record2 .play_data_title {
        font-size: 7.14px;
    }

    .war_record2 .label,
    .war_record2 .play_data_label {
        margin-top: 1px;
        font-size: 6.12px;
    }

    .weapon,
    .trait {
        height: 24px;
        display: inline-block;
        width: 24px;
    }

    .war_record2 .item_box {
        margin-left: 16px;
        margin-top: 10px;
        width: 102.4px;
        height: 41.6px;
    }

    .war_record2 .item {
        width: 27.4px;
        height: 17.4px;
    }


    .war_record2 .item_img {
        width: 24.6px;
        height: 14.4px;
        bottom: 22px;
        left: 2px;
    }



    .skill {
        margin-top: 20px;
        margin-left: 5px;
        width: 54px;
        height: 54px;
        float: left;
    }

    img {
        width: 100%;
        height: 100%;
    }

    .play_stat {
        margin-left: 20px;
        width: 100px;
        float: left;
        height: 54px;
        margin-top: 25px;
    }

    .stat {
        width: 80px;
        height: 14px;
        font-size: 14px;
        font-weight: bold;
    }

    .stat span {
        font-weight: normal;
    }

    .label {
        margin-top: 8px;
        width: 70px;
        height: 12px;
        font-size: 12px;
        color: #808080;
    }

    .play_data {
        border: #323232;
        margin-top: 25px;
        margin-left: 10px;
        width: 250px;
        height: 44px;
        float: left;
    }



    .damage {
        width: 62px;
        height: 44px;
        display: inline-block;
    }

    .rp {
        width: 70px;
        height: 44px;
        display: inline-block;
    }

    .rp svg {
        width: 8px;
        height: 5px;
    }

    .route {
        display: inline-block;
        width: 100px;
        height: 44px;
    }

    .play_data_title {
        font: 14px;
        font-weight: bold;
    }

    .play_data_label {
        margin-top: 3px;
        font-size: 12px;
        color: #808080;
    }

    .rp span {
        font-size: 13px;
        font-weight: normal;
        color: red;
    }

    .item_box {
        margin-left: 20px;
        margin-top: 20px;
        width: 128px;
        float: left;
        height: 52px;
    }

    .item {
        background-color: #E6E6E6;
        border-radius: 4px;
        float: left;
        width: 40.5px;
        height: 25.5px;
        list-style: none;
    }



    .item_box>.item:nth-child(1),
    .item_box>.item:nth-child(2),
    .item_box>.item:nth-child(3) {
        margin-bottom: 2px;
    }

    .item_box>.item:nth-child(2),
    .item_box>.item:nth-child(3),
    .item_box>.item:nth-child(5) {
        margin-left: 2px;
    }

    .item_bg {
        position: relative;
        mix-blend-mode: multiply;
        z-index: 1;
    }



    .item_img {
        position: relative;
        width: 32px;
        height: 18px;
        bottom: 25px;
        left: 4px;
        z-index: 2;
    }

    .teammate_name {
        float: left;
        width: 80px;
        margin-left: 20px;
        margin-top: 10px;
        font-size: 14px;
        text-align: center;
    }

    .teammate_name .character_name {
        font-size: 9px;
        color: #323232;
    }

    .game_id {
        float: left;
        font-size: 14px;
    }

    #recent_play {
        margin-top: 10px;
        border-collapse: collapse;
        background-color: rgb(255, 255, 255);
        width: 100%;
        font-size: 12px;
        font-weight: normal;
    }



    #recent_play>thead {
        color: rgb(255, 255, 255);
        background: rgb(54, 57, 68);
        border: 1px solid rgb(54, 57, 68);
        height: 40px;
    }

    #recent_play>tbody>tr {
        border: 1px solid rgb(171, 171, 182);
    }


    #recent_play>tbody>tr>td {
        height: 40px;
    }

    .character {
        text-indent: 10px;
        text-align: left;
    }

    .info {
        overflow: hidden;
        margin-top: 5px;
        margin-left: 8px;
        float: left;
        height: 38px;
        font-size: 13px;
    }

    .plays {
        font-size: 12px;
        color: #646464;
    }

    .avg-rank {
        padding-right: 10px;
        text-align: right;
    }

    .win-rate {
        text-align: center;
    }

    .image-wrapper,
    .image-wrapper>img {
        margin-left: 10px;
        border-radius: 50%;
        float: left;
        height: 40px;
        width: 40px;
    }
</style>