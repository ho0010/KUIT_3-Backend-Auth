package kuit3.backend.service;

import kuit3.backend.common.exception.StoreException;
import kuit3.backend.dao.StoreDao;
import kuit3.backend.dto.store.GetStoreResponse;
import kuit3.backend.dto.store.PostStoreRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static kuit3.backend.common.response.status.BaseExceptionResponseStatus.DUPLICATE_STORENAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreDao storeDao;

    public long registerStore(PostStoreRequest storeRequest) {
        if (storeDao.hasDuplicateStoreName(storeRequest.getName())) {
            throw new StoreException(DUPLICATE_STORENAME, "가게 이름이 중복됩니다.");
        }
        return storeDao.createStore(storeRequest);
    }

    public GetStoreResponse getStoreById(long storeId) {
        log.info("[StoreService.getStoreById]", storeId);
        return storeDao.findStoreById(storeId);
    }
    /*
    public List<GetStoreResponse> getAllStores() {
        return storeDao.findAllStores();
    }
    */
    private void validateStoreName(String storename) {
        if (storeDao.hasDuplicateStoreName(storename)) {
            throw new StoreException(DUPLICATE_STORENAME);
        }
    }

    public void modifyFoodCategory(long storeId, String foodCategory) {
        int affectedRows = storeDao.modifyFoodCategory(storeId, foodCategory);
        if (affectedRows != 1) {
            throw new RuntimeException("store 업데이트 실패");
        }
    }

    public String getStoreAddress(long storeId) {
        return storeDao.findStoreAddressById(storeId)
                .orElseThrow(() -> new RuntimeException("Store with ID " + storeId + " not found"));
    }

    public List<GetStoreResponse> findAllStoresFromIndex(Optional<Integer> endIndex, int limit) {
        int offset = endIndex.orElse(0); // 기본값 0
        return storeDao.findStoresFromIndex(offset, limit);
    }
}
