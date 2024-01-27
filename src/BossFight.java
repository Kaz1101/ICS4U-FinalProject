//public class BossFight extends GameObject{
//    private enum Stage {FIRST, SECOND, THIRD};
//    private Stage stage = Stage.FIRST;
//    public BossFight(String[] temp) {
//        super(temp);
//    }
//
//    @Override
//    public void doTick() {
//        super.doTick();
//        switch(stage){
//            case FIRST -> {
//                if(super.cur_hp < super.max_hp * 0.5){
//                    stage = Stage.SECOND;
//                    super.changeForm(2);
//                }
//                break;
//            }
//            case SECOND -> {
//                if(super.cur_hp < super.max_hp * 0.3){
//                    stage = Stage.THIRD;
//                    super.changeForm(3);
//                }
//                break;
//            }
//        }
//    }
//}
