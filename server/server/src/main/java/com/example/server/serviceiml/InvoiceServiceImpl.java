package com.example.server.serviceiml;

import com.example.server.dto.InvoiceResponse;
import com.example.server.dto.PaidInvoiceDTO;
import com.example.server.entity.*;
import com.example.server.enums.*;
import com.example.server.repository.*;
import com.example.server.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final ContractCustomerRepository contractCustomerRepository;
    private final BuildingServiceRepository buildingServiceRepository;
    private final MeterReadingRepository meterReadingRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    @Override
    @Transactional
    public void generateInvoices(Long buildingId, String month) {

        List<Rooms> rooms = roomRepository.findByBuildingId(buildingId);

        List<BuildingService> services =
                buildingServiceRepository.findByBuildingId(buildingId);

        for (Rooms room : rooms) {

            // ❌ đã có hóa đơn thì bỏ qua
            if (invoiceRepository.existsByRoomIdAndMonth(room.getId(), month)) {
                continue;
            }

            // lấy contract ACTIVE
            Optional<Contract> contractOpt =
                    contractRepository.findByRoom_IdAndStatus(room.getId(), ContractStatus.ACTIVE);

            if (contractOpt.isEmpty()) continue;

            Contract contract = contractOpt.get();

            int people = contractCustomerRepository
                    .findByContract_Id(contract.getId())  // ✅ đúng
                    .size();

            double total = contract.getRentPrice().doubleValue();

            // ✅ dùng InvoiceEntity
            InvoiceEntity invoice = new InvoiceEntity();
            invoice.setRoom(room);
            invoice.setContract(contract);
            invoice.setMonth(month);
            invoice.setStatus(InvoiceStatus.UNPAID);
            invoice.setTotalAmount(BigDecimal.ZERO);

            invoiceRepository.save(invoice);

            double serviceTotal = 0;

            for (BuildingService bs : services) {

                ServiceEntity service = bs.getService();
                double amount = 0;

                switch (service.getCalculationType()) {

                    case BY_METER:
                        MeterReading m = meterReadingRepository
                                .findByRoomAndServiceAndMonth(room, service, month)
                                .orElse(null);

                        if (m != null) {
                            amount = (m.getNewValue() - m.getOldValue())
                                    * bs.getPrice().doubleValue();
                        }
                        break;

                    case BY_PERSON:
                        amount = people * bs.getPrice().doubleValue();
                        break;

                    case FIXED:
                        amount = bs.getPrice().doubleValue();
                        break;
                }

                serviceTotal += amount;

                // ✅ dùng InvoiceDetailEntity
                InvoiceDetailEntity detail = new InvoiceDetailEntity();
                detail.setInvoice(invoice);
                detail.setServiceName(service.getServiceName());
                detail.setQuantity(BigDecimal.valueOf(1));
                detail.setUnitPrice(bs.getPrice());
                detail.setAmount(BigDecimal.valueOf(amount));

                invoiceDetailRepository.save(detail);
            }

            total += serviceTotal;

            invoice.setTotalAmount(BigDecimal.valueOf(total));
            invoiceRepository.save(invoice);
        }
    }

    @Override
    public List<InvoiceResponse> getInvoices(Long buildingId, String month) {

        return invoiceRepository
                .findByRoom_Building_IdAndMonth(buildingId, month)
                .stream()
                .map(inv -> {

                    // 👉 lấy user từ building
                    User user = inv.getRoom()
                            .getBuilding()
                            .getUser();

                    return new InvoiceResponse(
                            inv.getId(),
                            inv.getRoom().getRoomName(),
                            inv.getMonth(),
                            inv.getTotalAmount(),
                            inv.getStatus(),
                            user.getBankCode(),
                            user.getBankAccount()
                    );
                })
                .toList();
    }
    @Override
    @Transactional
    public void updateStatus(Long id, String status, String paymentMethod) {

        InvoiceEntity invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Hóa đơn đã thanh toán, không thể thay đổi!");
        }

        if (!"PAID".equals(status)) {
            throw new RuntimeException("Chỉ được chuyển sang PAID");
        }

        if (paymentMethod == null) {
            throw new RuntimeException("Thiếu phương thức thanh toán");
        }

        // ✅ Update
        invoice.setStatus(InvoiceStatus.PAID);
        invoice.setPaidAt(LocalDateTime.now());

        // ⚠️ convert String -> Enum
        invoice.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
    }
    public List<PaidInvoiceDTO> getPaidInvoicesByRoom(Long roomId) {

        return invoiceRepository
                .findTop5ByRoom_IdAndStatusOrderByPaidAtDesc(
                        roomId,
                        InvoiceStatus.PAID
                )
                .stream()
                .map(PaidInvoiceDTO::new)
                .toList();
    }
}